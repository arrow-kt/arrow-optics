package arrow.optics.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.data.ListK
import arrow.data.k
import arrow.instances.list.functor.`as`
import arrow.instances.option.applicative.applicative
import arrow.optics.Prism
import arrow.optics.Traversal
import arrow.optics.instances.listk.snoc.snoc
import arrow.test.UnitSpec
import arrow.typeclasses.Applicative
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.forAll
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.properties.map
import org.junit.runner.RunWith

operator fun <A> LinkedList<A>.plus(a: A): LinkedList<A> = when (this) {
  is Nil -> TCons(a, Nil)
  is TCons -> TCons(this.head, this.aas + a)
}

operator fun <A> LinkedList<A>.plus(a: LinkedList<A>): LinkedList<A> = when (this) {
  is Nil -> a
  is TCons -> TCons(this.head, this.aas + a)
}

sealed class LinkedList<out A> {

  val size: Int
    get() = when (this) {
      is Nil -> 0
      is TCons -> 1 + this.aas.size
    }

  val init: Option<LinkedList<A>>
    get() {
      tailrec fun go(prev: LinkedList<A>, left: LinkedList<A>): LinkedList<A> = when (left) {
        is Nil -> prev
        is TCons -> when (left.aas) {
          is Nil -> prev
          is TCons -> go(prev + left.head, left.aas)
        }
      }

      return when (this) {
        is Nil -> None
        is TCons -> go(Nil, this).some()
      }
    }

  val lastOption: Option<A>
    get() {
      fun go(left: TCons<A>): A = when (left.aas) {
        is Nil -> left.head
        is TCons -> go(left.aas)
      }

      return when (this) {
        is Nil -> None
        is TCons -> go(this).some()
      }
    }

}

data class TCons<A>(val head: A, val aas: LinkedList<A>) : LinkedList<A>()
object Nil : LinkedList<Nothing>()

fun <A> nil(): LinkedList<A> = Nil

fun <A> List<A>.asLinked() = this.asReversed().fold(nil<A>()) { acc, a -> TCons(a, acc) }

fun <A> listPlated(): Plated<LinkedList<A>> = object : Plated<LinkedList<A>> {
  override fun plate() = object : Traversal<LinkedList<A>, LinkedList<A>> {
    override fun <F> modifyF(FA: Applicative<F>, s: LinkedList<A>, f: (LinkedList<A>) -> Kind<F, LinkedList<A>>): Kind<F, LinkedList<A>> = when (s) {
      is TCons -> FA.run { f(s.aas).map { TCons(s.head, it) } }
      is Nil -> FA.just(Nil)
    }
  }
}

fun <A> ListK.Companion.plated(): Plated<ListK<A>> = object : Plated<ListK<A>> {
  override fun plate() = object : Traversal<ListK<A>, ListK<A>> {
    override fun <F> modifyF(FA: Applicative<F>, s: ListK<A>, f: (ListK<A>) -> Kind<F, ListK<A>>): Kind<F, ListK<A>> = when {
      s.isNotEmpty() -> FA.run { f(s.drop(1).k()).map { ListK(listOf(s.first()) + it) } }
      else -> FA.just(empty())
    }
  }
}

fun <A> listSnoc(): Snoc<LinkedList<A>, A> = object : Snoc<LinkedList<A>, A> {
  override fun snoc(): Prism<LinkedList<A>, Tuple2<LinkedList<A>, A>> = Prism(
    getOrModify = { Option.applicative().map(it.init, it.lastOption) { (a, b) -> Tuple2(a, b) }.fix().toEither { it } },
    reverseGet = { (aas, a) -> aas + a }
  )
}

@RunWith(KTestJUnitRunner::class)
class PlatedTest : UnitSpec() {

  init {
    "LinkedList init" {
      TCons(1, TCons(2, TCons(3, Nil))).init shouldBe TCons(1, TCons(2, Nil)).some()
      listOf(1, 2, 3).asLinked() shouldBe TCons(1, TCons(2, TCons(3, Nil)))
      Nil.init shouldBe none<Int>()

      TCons(1, TCons(2, TCons(3, Nil))).lastOption shouldBe 3.some()
      TCons(1, Nil).lastOption shouldBe 1.some()
      Nil.lastOption shouldBe none<Int>()
    }

    "children on LinkedList is consistent with .tail" {
      forAll(Gen.int(), Gen.list(Gen.int()).map { it.asLinked() }) { h, t ->
        val s = TCons(h, t)
        listPlated<Int>().run {
          s.children == listOf(s.aas)
        }
      }
    }

    "universe on Stream is consistent with .tails" {
      forAll(Gen.list(Gen.int()).map { it.asLinked() }) { s ->
        listPlated<Int>().run {
          s.universe == s.children.asSequence()
        }
      }
    }

    "rewrite on Stream is able to change the last node" {
      forAll(Gen.int(), Gen.int(), Gen.int()) { x, y, z ->
        listPlated<Int>().run {
          TCons(x, TCons(y, TCons(z, Nil))).rewrite {
            when (it) {
              is TCons -> if (it.head != x) Some(TCons(x, Nil)) else None
              else -> None
            }
          }
        } == TCons(x, TCons(y, TCons(x, Nil)))
      }
    }

    "rewriteOf initOption on Stream is able to change the first node" {
      forAll(Gen.int(), Gen.int(), Gen.int()) { x, y, z ->
        listPlated<Int>().run {
          TCons(x, TCons(y, TCons(z, Nil))).rewriteOf(listSnoc<Int>().initOption().asSetter()) {
            when (it) {
              is TCons -> if (it.head != z) Some(TCons(z, Nil)) else None
              else -> None
            }
          }
        } == TCons(z, TCons(y, TCons(z, Nil)))
      }
    }

    "transform on Stream can change the last element without a guard" {
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.list(Gen.int()).map { it.asLinked() }) { i, x, y, xs ->
        listPlated<Int>().run {
          (xs + TCons(y, TCons(x, Nil))).transform {
            when (it) {
              is Nil -> Nil
              is TCons -> if (it.head == x && it.aas == Nil) TCons(i, it.aas) else it
            }
          }
        } == xs + TCons(y, TCons(i, Nil))
      }
    }

    "transform initOption on Stream can change the first element without a guard" {
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.list(Gen.int())) { i, x, y, xs ->
        listPlated<Int>().run {
          (TCons(x, TCons(y, xs.asLinked())))
            .transformOf(listSnoc<Int>().initOption().asSetter()) {
              when (it) {
                is Nil -> Nil
                is TCons -> if (it.head == x) TCons(i, it.aas) else it
              }
            } == (listOf(i) + listOf(y) + xs).asLinked()
        }
      }
    }

    "transform counting Stream using Option.pure returns count of changes the same as stream size" {
      forAll(Gen.list(Gen.int()).map { it.asLinked() }) { xs ->
        listPlated<Int>().run {
          xs.transformCounting(::Some) == Tuple2(xs.size, xs)
        }
      }
    }

  }
}
