package arrow.optics.extensions

import arrow.Kind
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.extension
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative
import kotlin.reflect.KClass

/**
 * [Traversal] for [Sequence] that has focus in each [A].
 *
 * @receiver KClass of [Sequence] to make it statically available.
 * @return [Traversal] with source [Sequence] and focus in every [A] of the source.
 */
fun <A> KClass<Sequence<A>>.traversal(): Traversal<Sequence<A>, A> = object : Traversal<Sequence<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: Sequence<A>, f: (A) -> Kind<F, A>): Kind<F, Sequence<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [Sequence].
 */
@extension
interface SequenceEach<A> : Each<Sequence<A>, A> {
  override fun each(): Traversal<Sequence<A>, A> =
    Sequence::class.traversal()
}

/**
 * [FilterIndex] instance definition for [Sequence].
 */
@extension
interface SequenceFilterIndex<A> : FilterIndex<Sequence<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<Sequence<A>, A> = object : Traversal<Sequence<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: Sequence<A>, f: (A) -> Kind<F, A>): Kind<F, Sequence<A>> = FA.run {
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
        if (p(j)) f(a) else just(a)
      }
    }
  }
}

/**
 * [Index] instance definition for [Sequence].
 */
@extension
interface SequenceIndex<A> : Index<Sequence<A>, Int, A> {
  override fun index(i: Int): Optional<Sequence<A>, A> = POptional(
    getOrModify = { it.elementAtOrNull(i)?.right() ?: it.left() },
    set = { s, a -> s.mapIndexed { index, aa -> if (index == i) a else aa }.k() }
  )
}
