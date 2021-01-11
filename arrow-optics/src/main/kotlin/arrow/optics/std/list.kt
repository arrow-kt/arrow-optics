package arrow.optics

import arrow.Kind
import arrow.core.Either
import arrow.core.ListExtensions
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.extensions.option.applicative.applicative
import arrow.core.fix
import arrow.core.identity
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import arrow.core.toT
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Applicative
import kotlin.reflect.KClass

/**
 * [Optional] to safely operate on the head of a list
 */
@Deprecated(
  "ListK is being deprecated, use the function defined for List instead.",
  ReplaceWith("List::class.head<A>()"),
  DeprecationLevel.WARNING)
fun <A> ListK.Companion.head(): Optional<List<A>, A> = Optional(
  getOption = { Option.fromNullable(it.firstOrNull()) },
  set = { list, newHead -> list.mapIndexed { index, value -> if (index == 0) newHead else value } }
)

/**
 * [Optional] to safely operate on the head of a list
 */
fun <A> KClass<List<*>>.head(): Optional<List<A>, A> = Optional(
  getOption = { Option.fromNullable(it.firstOrNull()) },
  set = { list, newHead -> list.mapIndexed { index, value -> if (index == 0) newHead else value } }
)

/**
 * [Optional] to safely operate on the tail of a list
 */
@Deprecated(
  "ListK is being deprecated, use the function defined for List instead.",
  ReplaceWith("List::class.tail<A>()"),
  DeprecationLevel.WARNING)
fun <A> ListK.Companion.tail(): Optional<List<A>, List<A>> = Optional(
  getOption = { if (it.isEmpty()) None else Some(it.drop(1)) },
  set = { list, newTail ->
    list.firstOrNull()?.let {
      listOf(it) + newTail
    } ?: emptyList()
  }
)

/**
 * [Optional] to safely operate on the tail of a list
 */
fun <A> KClass<List<*>>.tail(): Optional<List<A>, List<A>> = Optional(
  getOption = { if (it.isEmpty()) None else Some(it.drop(1)) },
  set = { list, newTail ->
    list.firstOrNull()?.let {
      listOf(it) + newTail
    } ?: emptyList()
  }
)

/**
 * [PIso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
@Deprecated(
  "ListK is being deprecated, use the function defined for List instead.",
  ReplaceWith("List::class.toPOptionNel<A, B>()"),
  DeprecationLevel.WARNING)
fun <A, B> ListK.Companion.toPOptionNel(): PIso<List<A>, List<B>, Option<NonEmptyList<A>>, Option<NonEmptyList<B>>> = PIso(
  get = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) },
  reverseGet = { optNel -> optNel.fold({ emptyList() }, NonEmptyList<B>::all) }
)

/**
 * [PIso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A, B> KClass<List<*>>.toPOptionNel(): PIso<List<A>, List<B>, Option<NonEmptyList<A>>, Option<NonEmptyList<B>>> = PIso(
  get = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) },
  reverseGet = { optNel -> optNel.fold({ emptyList() }, NonEmptyList<B>::all) }
)

/**
 * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
@Deprecated(
  "ListK is being deprecated, use the function defined for List instead.",
  ReplaceWith("List::class.toPOptionNel<A>()"),
  DeprecationLevel.WARNING)
fun <A> ListK.Companion.toOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> = toPOptionNel()

/**
 * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A> KClass<List<*>>.toOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> = toPOptionNel()

/**
 * [PIso] that defines the equality between a [List] and a [ListK]
 */
@Deprecated(
  "ListK is being deprecated, and this function will be removed in 0.13.0.",
  level = DeprecationLevel.WARNING)
fun <A, B> ListExtensions.toPListK(): PIso<List<A>, List<B>, ListK<A>, ListK<B>> = PIso(
  get = List<A>::k,
  reverseGet = ::identity
)

/**
 * [Iso] that defines the equality between a [List] and a [ListK]
 */
@Deprecated(
  "ListK is being deprecated, and this function will be removed in 0.13.0.",
  level = DeprecationLevel.WARNING)
fun <A> ListExtensions.toListK(): Iso<List<A>, ListK<A>> = toPListK()

fun <A> KClass<List<*>>.traversal(): Traversal<List<A>, A> = listTraversal()

/**
 * [Traversal] for [List] that focuses in each [A] of the source [List].
 */
fun <A> listTraversal(): Traversal<List<A>, A> = object : Traversal<List<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
    s.k().traverse(FA, f)
}

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
