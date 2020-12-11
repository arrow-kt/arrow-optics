package arrow.optics.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.ListExtensions
import arrow.core.Option
import arrow.core.Predicate
import arrow.core.Tuple2
import arrow.core.extensions.list.traverse.traverse
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import arrow.core.toT
import arrow.core.extensions.option.applicative.applicative
import arrow.core.k
import arrow.core.fix
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.PPrism
import arrow.optics.Prism
import arrow.optics.Traversal
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import kotlin.reflect.KClass

@Deprecated("Instance should be obtained through List class", ReplaceWith("List::class.traversal()"))
fun <A> ListExtensions.traversal(): Traversal<List<A>, A> = ListTraversal()

fun <A> KClass<List<*>>.traversal(): Traversal<List<A>, A> = ListTraversal()

/**
 * [Traversal] for [List] that focuses in each [A] of the source [List].
 */
interface ListTraversal<A> : Traversal<List<A>, A> {

  override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
    FA.run {
      s.traverse(FA, f).map { it.fix() }
    }

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <A> invoke() = object : ListTraversal<A> {}
  }
}

@Deprecated("Instance should be obtained through List class", ReplaceWith("List::class.each()"))
fun <A> ListExtensions.each(): Each<List<A>, A> = listEach()

fun <A> KClass<List<*>>.each(): Each<List<A>, A> = listEach()

/**
 * [Each] instance definition for [List] that summons a [Traversal] to focus in each [A] of the source [List].
 */
inline fun <A> listEach(): Each<List<A>, A> = Each { List::class.traversal() }

@Deprecated("Instance should be obtained through List class", ReplaceWith("List::class.filterIndex()"))
fun <A> ListExtensions.filterIndex(): FilterIndex<List<A>, Int, A> = List::class.filterIndex()

fun <A> KClass<List<*>>.filterIndex(): FilterIndex<List<A>, Int, A> = listFilterIndex()

/**
 * [FilterIndex] instance definition for [List].
 */
inline fun <A> listFilterIndex(): FilterIndex<List<A>, Int, A> = FilterIndex { p ->
  object : Traversal<List<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
        if (p(j)) f(a) else FA.just(a)
      }
  }
}

fun <A> KClass<List<*>>.filter(p: Predicate<Int>): Traversal<List<A>, A> =
  List::class.filterIndex<A>().filter(p)

@Deprecated("Instance should be obtained through List class", ReplaceWith("List::class.index()"))
fun <A> ListExtensions.index(): Index<List<A>, Int, A> = listIndex()

fun <A> KClass<List<*>>.index(): Index<List<A>, Int, A> = listIndex()

fun <A> KClass<List<*>>.index(i: Int): Optional<List<A>, A> = List::class.index<A>().index(i)

/**
 * [Index] instance definition for [List].
 */
inline fun <A> listIndex(): Index<List<A>, Int, A> = Index { i ->
  POptional(
    getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
    set = { l, a -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa } }
  )
}

@Deprecated("Instance should be obtained through List class", ReplaceWith("List::class.cons()"))
fun <A> ListExtensions.cons(): Cons<List<A>, A> = listCons()

fun <A> KClass<List<*>>.cons(): Cons<List<A>, A> = listCons()

/**
 * [Cons] instance definition for [List].
 */
inline fun <A> listCons(): Cons<List<A>, A> = Cons {
  PPrism(
    getOrModify = { list -> list.firstOrNull()?.let { Tuple2(it, list.drop(1)) }?.right() ?: list.left() },
    reverseGet = { (a, aas) -> listOf(a) + aas }
  )
}

infix fun <A> A.cons(tail: List<A>): List<A> = listCons<A>().run { this@cons.cons(tail) }

fun <A> KClass<List<*>>.firstOption(): POptional<List<A>, List<A>, A, A> = List::class.cons<A>().firstOption()

fun <A> KClass<List<*>>.tailOption(): POptional<List<A>, List<A>, List<A>, List<A>> = List::class.cons<A>().tailOption()

fun <A> List<A>.uncons(): Option<Tuple2<A, List<A>>> = listCons<A>().run { this@uncons.uncons() }

@Deprecated("Instance should be obtained through List class", ReplaceWith("List::class.snoc()"))
fun <A> ListExtensions.snoc(): Snoc<List<A>, A> = listSnoc()

fun <A> KClass<List<*>>.snoc(): Snoc<List<A>, A> = listSnoc()

/**
 * [Snoc] instance definition for [List].
 */
inline fun <A> listSnoc(): Snoc<List<A>, A> = Snoc {
  object : Prism<List<A>, Tuple2<List<A>, A>> {
    override fun getOrModify(s: List<A>): Either<List<A>, Tuple2<List<A>, A>> =
      Option.applicative().mapN(Option.just(s.dropLast(1)), s.lastOrNull().toOption(), ::identity)
        .fix()
        .toEither { s }

    override fun reverseGet(b: Tuple2<List<A>, A>): List<A> =
      b.a + b.b
  }
}

infix fun <A> List<A>.snoc(last: A): List<A> = listSnoc<A>().run { this@snoc.snoc(last) }

fun <A> KClass<List<*>>.initOption(): POptional<List<A>, List<A>, List<A>, List<A>> = List::class.snoc<A>().initOption()

fun <A> KClass<List<*>>.lastOption(): POptional<List<A>, List<A>, A, A> = List::class.snoc<A>().lastOption()

fun <A> List<A>.unsnoc(): Option<Tuple2<List<A>, A>> = listSnoc<A>().run { this@unsnoc.unsnoc() }

// TODO: List Eq should be in Arrow Core
interface ListEq<A> : Eq<List<A>> {
  fun EQA(): Eq<A>

  override fun List<A>.eqv(b: List<A>): Boolean =
    if (this.size == b.size) {
      this.zip(b).all { (a, b) -> EQA().run { a.eqv(b) } }
    } else {
      false
    }

  companion object {
    operator fun <A> invoke(eqA: Eq<A>): ListEq<A> = object : ListEq<A> {
      override fun EQA(): Eq<A> = eqA
    }
  }
}

fun <A> KClass<List<*>>.eq(eqA: Eq<A>): ListEq<A> = ListEq(eqA)
