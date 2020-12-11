package arrow.optics.extensions

import arrow.Kind
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.core.NonEmptyList
import arrow.extension
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.extensions.nonemptylist.filterIndex.filterIndex
import arrow.optics.extensions.nonemptylist.index.index
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [NonEmptyList] that has focus in each [A].
 *
 * @receiver [NonEmptyList.Companion] to make it statically available.
 * @return [Traversal] with source [NonEmptyList] and focus every [A] of the source.
 */
fun <A> NonEmptyList.Companion.traversal(): Traversal<NonEmptyList<A>, A> = object : Traversal<NonEmptyList<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: NonEmptyList<A>, f: (A) -> Kind<F, A>): Kind<F, NonEmptyList<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [NonEmptyList].
 */
@extension
interface NonEmptyListEach<A> : Each<NonEmptyList<A>, A> {
  override fun each(): Traversal<NonEmptyList<A>, A> =
    NonEmptyList.traversal()
}

inline fun <A> NonEmptyList<A>.each(): Each<NonEmptyList<A>, A> = Each { NonEmptyList.traversal() }

/**
 * [FilterIndex] instance definition for [NonEmptyList].
 */
@extension
interface NonEmptyListFilterIndex<A> : FilterIndex<NonEmptyList<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<NonEmptyList<A>, A> = object : Traversal<NonEmptyList<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: NonEmptyList<A>, f: (A) -> Kind<F, A>): Kind<F, NonEmptyList<A>> =
      s.all.mapIndexed { index, a -> a toT index }
        .let(NonEmptyList.Companion::fromListUnsafe)
        .traverse(FA) { (a, j) -> if (p(j)) f(a) else FA.just(a) }
  }
}

fun <A> filter(p: Function1<Int, Boolean>): Traversal<NonEmptyList<A>, A> = NonEmptyList.filterIndex<A>().filter(p)

/**
 * [Index] instance definition for [NonEmptyList].
 */
@extension
interface NonEmptyListIndex<A> : Index<NonEmptyList<A>, Int, A> {
  override fun index(i: Int): Optional<NonEmptyList<A>, A> = POptional(
    getOrModify = { l -> l.all.getOrNull(i)?.right() ?: l.left() },
    set = { l, a ->
      NonEmptyList.fromListUnsafe(
        l.all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
      )
    }
  )
}

fun <A> index(i: Int): Optional<NonEmptyList<A>, A> = NonEmptyList.index<A>().index(i)

operator fun <A, T> Lens<T, NonEmptyList<A>>.get(i: Int): Optional<T, A> =
  NonEmptyList.index<A>().run { this@get.get<T>(i) }
