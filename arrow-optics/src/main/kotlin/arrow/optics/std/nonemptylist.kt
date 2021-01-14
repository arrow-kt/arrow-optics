package arrow.optics

import arrow.core.NonEmptyList
import arrow.core.Predicate
import arrow.core.left
import arrow.core.right
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Monoid

/**
 * [Lens] to operate on the head of a [NonEmptyList]
 */
@Deprecated(
  "Use the nonEmptyListHead function exposed in the Lens' companion object",
  ReplaceWith(
    "Lens.nonEmptyListHead<A>()",
    "arrow.optics.Lens", "arrow.optics.nonEmptyListHead"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList.Companion.head(): Lens<NonEmptyList<A>, A> = Lens(
  get = NonEmptyList<A>::head,
  set = { nel, newHead -> NonEmptyList(newHead, nel.tail) }
)

/**
 * [Lens] to operate on the head of a [NonEmptyList]
 */
fun <A> PLens.Companion.nonEmptyListHead(): Lens<NonEmptyList<A>, A> = Lens(
  get = NonEmptyList<A>::head,
  set = { nel, newHead -> NonEmptyList(newHead, nel.tail) }
)

/**
 * [Lens] to operate on the tail of a [NonEmptyList]
 */
@Deprecated(
  "Use the nonEmptyListTail function exposed in the Lens' companion object",
  ReplaceWith(
    "Lens.nonEmptyListTail<A>()",
    "arrow.optics.Lens", "arrow.optics.nonEmptyListTail"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList.Companion.tail(): Lens<NonEmptyList<A>, List<A>> = Lens(
  get = NonEmptyList<A>::tail,
  set = { nel, newTail -> NonEmptyList(nel.head, newTail) }
)

/**
 * [Lens] to operate on the tail of a [NonEmptyList]
 */
fun <A> PLens.Companion.nonEmptyListTail(): Lens<NonEmptyList<A>, List<A>> = Lens(
  get = NonEmptyList<A>::tail,
  set = { nel, newTail -> NonEmptyList(nel.head, newTail) }
)

/**
 * [Traversal] for [NonEmptyList] that has focus in each [A].
 *
 * @receiver [PTraversal.Companion] to make it statically available.
 * @return [Traversal] with source [NonEmptyList] and focus every [A] of the source.
 */
fun <A> PTraversal.Companion.nonEmptyList(): Traversal<NonEmptyList<A>, A> =
  Traversal { s, f -> s.map(f) }

fun <A> Fold.Companion.nonEmptyList(): Fold<NonEmptyList<A>, A> = object : Fold<NonEmptyList<A>, A> {
  override fun <R> foldMap(M: Monoid<R>, s: NonEmptyList<A>, f: (A) -> R): R =
    M.run { s.fold(empty()) { acc, r -> acc.combine(f(r)) } }
}

fun <A> PEvery.Companion.nonEmptyList(): Every<NonEmptyList<A>, A> = object : Every<NonEmptyList<A>, A> {
  override fun <R> foldMap(M: Monoid<R>, s: NonEmptyList<A>, f: (A) -> R): R =
    M.run { s.fold(empty()) { acc, r -> acc.combine(f(r)) } }

  override fun map(s: NonEmptyList<A>, f: (A) -> A): NonEmptyList<A> =
    s.map(f)
}

/**
 * [FilterIndex] instance definition for [NonEmptyList].
 */
fun <A> FilterIndex.Companion.nonEmptyList(): FilterIndex<NonEmptyList<A>, Int, A> = FilterIndex<NonEmptyList<A>, Int, A> { p ->
  object : Every<NonEmptyList<A>, A> {
    override fun <R> foldMap(M: Monoid<R>, s: NonEmptyList<A>, f: (A) -> R): R = M.run {
      s.foldIndexed(empty()) { index, acc, r ->
        if (p(index)) acc.combine(f(r)) else acc
      }
    }

    override fun map(s: NonEmptyList<A>, f: (A) -> A): NonEmptyList<A> =
      NonEmptyList.fromListUnsafe(s.mapIndexed { index, a -> if (p(index)) f(a) else a })
  }
}

/**
 * [Index] instance definition for [NonEmptyList].
 */
fun <A> Index.Companion.nonEmptyList(): Index<NonEmptyList<A>, Int, A> = Index { i ->
  POptional(
    getOrModify = { l -> l.all.getOrNull(i)?.right() ?: l.left() },
    set = { l, a ->
      NonEmptyList.fromListUnsafe(
        l.all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
      )
    }
  )
}
