package arrow.optics.extensions

import arrow.Kind
import arrow.core.Predicate
import arrow.core.extensions.sequence.traverse.traverse
import arrow.core.fix
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import kotlin.reflect.KClass

/**
 * [Traversal] for [Sequence] that has focus in each [A].
 *
 * @return [Traversal] with source [Sequence] and focus in every [A] of the source.
 */
fun <A> KClass<Sequence<*>>.traversal(): Traversal<Sequence<A>, A> = object : Traversal<Sequence<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: Sequence<A>, f: (A) -> Kind<F, A>): Kind<F, Sequence<A>> =
    // TODO: Revisit when Traversal has been refactored
    FA.run { s.traverse(FA, f).map { it.fix().sequence } }
}

/**
 * [Each] instance definition for [Sequence].
 */
fun <A> sequenceEach(): Each<Sequence<A>, A> = Each { Sequence::class.traversal() }

fun <A> KClass<Sequence<*>>.each(): Each<Sequence<A>, A> = sequenceEach<A>()

/**
 * [FilterIndex] instance definition for [Sequence].
 */
fun <A> sequenceFilterIndex(): FilterIndex<Sequence<A>, Int, A> = FilterIndex { p ->
  object : Traversal<Sequence<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: Sequence<A>, f: (A) -> Kind<F, A>): Kind<F, Sequence<A>> = FA.run {
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
        if (p(j)) f(a) else just(a)
      }
    }
  }
}

fun <A> KClass<Sequence<*>>.filterIndex(): FilterIndex<Sequence<A>, Int, A> = sequenceFilterIndex<A>()

fun <A> KClass<Sequence<*>>.filter(p: Predicate<Int>): Traversal<Sequence<A>, A> =
  Sequence::class.filterIndex<A>().filter(p)

/**
 * [Index] instance definition for [Sequence].
 */
fun <A> sequenceIndex(): Index<Sequence<A>, Int, A> = Index { i ->
  POptional(
    getOrModify = { it.elementAtOrNull(i)?.right() ?: it.left() },
    set = { s, a -> s.mapIndexed { index, aa -> if (index == i) a else aa } }
  )
}

fun <A> KClass<Sequence<*>>.index(): Index<Sequence<A>, Int, A> = sequenceIndex()

fun <A> KClass<Sequence<*>>.index(i: Int): Optional<Sequence<A>, A> = Sequence::class.index<A>().index(i)

interface SequenceEq<A> : Eq<Sequence<A>> {
  fun EQA(): Eq<A>

  override fun Sequence<A>.eqv(b: Sequence<A>): Boolean =
    if (this.count() == b.count()) {
      this.map { a ->
        b.find { x ->
          EQA().run { a.eqv(x) }
        } != null
      }.all { it }
    } else {
      false
    }

  companion object {
    operator fun <A> invoke(eqa: Eq<A>): SequenceEq<A> = object : SequenceEq<A> {
      override fun EQA(): Eq<A> = eqa
    }
  }
}

fun <A> KClass<Sequence<*>>.eq(eqa: Eq<A>): SequenceEq<A> = SequenceEq(eqa)
