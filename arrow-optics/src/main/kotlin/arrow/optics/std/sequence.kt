package arrow.optics

import arrow.core.left
import arrow.core.right
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Monoid

fun <A> FilterIndex.Companion.sequence(): FilterIndex<Sequence<A>, Int, A> = FilterIndex { p ->
  object : Every<Sequence<A>, A> {
    override fun <R> foldMap(M: Monoid<R>, s: Sequence<A>, f: (A) -> R): R = M.run {
      s.foldIndexed(empty()) { index, acc, a ->
        if (p(index)) acc.combine(f(a)) else acc
      }
    }

    override fun modify(s: Sequence<A>, f: (A) -> A): Sequence<A> =
      s.mapIndexed { index, a -> if (p(index)) f(a) else a }
  }
}

fun <A> Index.Companion.sequence(): Index<Sequence<A>, Int, A> = Index { i ->
  POptional(
    getOrModify = { it.elementAtOrNull(i)?.right() ?: it.left() },
    set = { s, a -> s.mapIndexed { index, aa -> if (index == i) a else aa } }
  )
}

fun <A> PTraversal.Companion.sequence(): Traversal<Sequence<A>, A> =
  Traversal { s, f -> s.map(f) }
