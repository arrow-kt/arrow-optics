package arrow.optics.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import arrow.core.toT
import arrow.core.ListK
import arrow.core.k
import arrow.core.fix
import arrow.core.extensions.option.applicative.applicative
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

/**
 * [Traversal] for [ListK] that has focus in each [A].
 *
 * @receiver [ListK.Companion] to make it statically available.
 * @return [Traversal] with source [ListK] and focus every [A] of the source.
 */
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.traversal",
  ReplaceWith(
    "List::class.traversal<A>()",
    "arrow.optics.traversal"),
  DeprecationLevel.WARNING)
fun <A> ListK.Companion.traversal(): Traversal<ListK<A>, A> = object : Traversal<ListK<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: ListK<A>, f: (A) -> Kind<F, A>): Kind<F, ListK<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [ListK].
 */
@Deprecated(
  "Each is being deprecated. Use Traversal directly instead.",
  ReplaceWith(
    "List::class.traversal<A>()",
    "arrow.optics.traversal"),
  DeprecationLevel.WARNING)
fun <A> listKEach(): Each<ListK<A>, A> = Each { ListK.traversal() }

/**
 * [FilterIndex] instance definition for [ListK].
 */
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.filterIndex",
  ReplaceWith(
    "List::class.filterIndex<A>()",
    "arrow.optics.filterIndex"),
  DeprecationLevel.WARNING)
fun <A> listKFilterIndex(): FilterIndex<ListK<A>, Int, A> = FilterIndex { p ->
  object : Traversal<ListK<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: ListK<A>, f: (A) -> Kind<F, A>): Kind<F, ListK<A>> = FA.run {
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
        if (p(j)) f(a) else just(a)
      }
    }
  }
}

/**
 * [Index] instance definition for [ListK].
 */
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.index",
  ReplaceWith(
    "List::class.index<A>()",
    "arrow.optics.index"),
  DeprecationLevel.WARNING)
fun <A> listKIndex(): Index<ListK<A>, Int, A> = Index { i ->
  POptional(
    getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
    set = { l, a -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() }
  )
}

/**
 * [Cons] instance definition for [ListK].
 */
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.cons",
  ReplaceWith(
    "List::class.cons<A>()",
    "arrow.optics.cons"),
  DeprecationLevel.WARNING)
fun <A> listKCons(): Cons<ListK<A>, A> = Cons {
  PPrism(
    getOrModify = { list -> list.firstOrNull()?.let { Tuple2(it, list.drop(1).k()) }?.right() ?: list.left() },
    reverseGet = { (a, aas) -> ListK(listOf(a) + aas) }
  )
}

/**
 * [Snoc] instance definition for [ListK].
 */
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.snoc",
  ReplaceWith(
    "List::class.snoc<A>()",
    "arrow.optics.snoc"),
  DeprecationLevel.WARNING)
fun <A> listKSnoc(): Snoc<ListK<A>, A> = Snoc {
  object : Prism<ListK<A>, Tuple2<ListK<A>, A>> {
    override fun getOrModify(s: ListK<A>): Either<ListK<A>, Tuple2<ListK<A>, A>> =
      Option.applicative().mapN(Option.just(s.dropLast(1).k()), s.lastOrNull().toOption(), ::identity)
        .fix()
        .toEither { s }

    override fun reverseGet(b: Tuple2<ListK<A>, A>): ListK<A> =
      ListK(b.a + b.b)
  }
}
