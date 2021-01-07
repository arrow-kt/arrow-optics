package arrow.optics

import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import arrow.core.toT
import arrow.core.extensions.option.applicative.applicative
import arrow.core.k
import arrow.core.fix
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Applicative
import kotlin.reflect.KClass

fun <A> KClass<List<*>>.traversal(): Traversal<List<A>, A> = ListTraversal()

/**
 * [Traversal] for [List] that focuses in each [A] of the source [List].
 */
interface ListTraversal<A> : Traversal<List<A>, A> {

  override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
    s.k().traverse(FA, f)

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <A> invoke() = object : ListTraversal<A> {}
  }
}

/**
 * [Each] instance definition for [List] that summons a [Traversal] to focus in each [A] of the source [List].
 */
fun <A> listEach(): Each<List<A>, A> = Each { ListTraversal() }

fun <A> KClass<List<*>>.each(): Each<List<A>, A> = listEach()

/**
 * [FilterIndex] instance definition for [List].
 */
fun <A> listFilterIndex(): FilterIndex<List<A>, Int, A> = FilterIndex { p ->
  object : Traversal<List<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
        if (p(j)) f(a) else FA.just(a)
      }
  }
}

fun <A> KClass<List<*>>.filterIndex(): FilterIndex<List<A>, Int, A> = listFilterIndex()

fun <A> KClass<List<*>>.filter(p: Function1<Int, Boolean>): PTraversal<List<A>, List<A>, A, A> =
  List::class.filterIndex<A>().filter(p)

/**
 * [Index] instance definition for [List].
 */
fun <A> listIndex(): Index<List<A>, Int, A> = Index { i ->
  POptional(
    getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
    set = { l, a -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa } }
  )
}

fun <A> KClass<List<*>>.index(): Index<List<A>, Int, A> = listIndex()

fun <A> KClass<List<*>>.index(i: Int): POptional<List<A>, List<A>, A, A> =
  List::class.index<A>().index(i)

operator fun <A, T> PLens<T, T, List<A>, List<A>>.get(i: Int): POptional<T, T, A, A> =
  List::class.index<A>().run { this@get.get(i) }

/**
 * [Cons] instance definition for [List].
 */
fun <A> listCons(): Cons<List<A>, A> = Cons {
  PPrism(
    getOrModify = { list -> list.firstOrNull()?.let { Tuple2(it, list.drop(1)) }?.right() ?: list.left() },
    reverseGet = { (a, aas) -> listOf(a) + aas }
  )
}

fun <A> KClass<List<*>>.cons(): Cons<List<A>, A> = listCons()

fun <A> KClass<List<*>>.firstOption(): POptional<List<A>, List<A>, A, A> =
  List::class.cons<A>().firstOption()

fun <A> KClass<List<*>>.tailOption(): POptional<List<A>, List<A>, List<A>, List<A>> =
  List::class.cons<A>().tailOption()

infix fun <A> A.cons(tail: List<A>): List<A> =
  List::class.cons<A>().run { this@cons.cons(tail) }

fun <A> List<A>.uncons(): Option<Tuple2<A, List<A>>> =
  List::class.cons<A>().run { this@uncons.uncons() }

/**
 * [Snoc] instance definition for [List].
 */
fun <A> listSnoc(): Snoc<List<A>, A> = Snoc {
  object : Prism<List<A>, Tuple2<List<A>, A>> {
    override fun getOrModify(s: List<A>): Either<List<A>, Tuple2<List<A>, A>> =
      Option.applicative().mapN(Option.just(s.dropLast(1)), s.lastOrNull().toOption(), ::identity)
        .fix()
        .toEither { s }

    override fun reverseGet(b: Tuple2<List<A>, A>): List<A> =
      b.a + b.b
  }
}

fun <A> KClass<List<*>>.snoc(): Snoc<List<A>, A> = listSnoc()

fun <A> KClass<List<*>>.initOption(): POptional<List<A>, List<A>, List<A>, List<A>> =
  List::class.snoc<A>().initOption()

fun <A> KClass<List<*>>.lastOption(): POptional<List<A>, List<A>, A, A> =
  List::class.snoc<A>().lastOption()

infix fun <A> List<A>.snoc(last: A): List<A> =
  List::class.snoc<A>().run { this@snoc.snoc(last) }

fun <A> List<A>.unsnoc(): Option<Tuple2<List<A>, A>> =
  List::class.snoc<A>().run { this@unsnoc.unsnoc() }
