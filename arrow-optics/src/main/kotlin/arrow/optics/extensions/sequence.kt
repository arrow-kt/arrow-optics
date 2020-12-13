package arrow.optics.extensions

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
import arrow.typeclasses.Eq
import kotlin.reflect.KClass

/**
 * [Traversal] for [SequenceK] that has focus in each [A].
 *
 * @receiver [SequenceK.Companion] to make it statically available.
 * @return [Traversal] with source [SequenceK] and focus in every [A] of the source.
 */
fun <A> KClass<Sequence<*>>.traversal(): Traversal<Sequence<A>, A> =
  Traversal { s, f ->
    // TODO: Revisit when Traversal has been refactored
    s.map(f)
  }

/**
 * [Each] instance definition for [SequenceK].
 */
@extension
interface SequenceEach<A> : Each<Sequence<A>, A> {
  @Suppress("UNCHECKED_CAST")
  override fun each(): Traversal<Sequence<A>, A> =
    Sequence::class.traversal()

  companion object {
    operator fun <A> invoke(): SequenceEach<A> = object : SequenceEach<A> {}
  }
}

fun <A> KClass<Sequence<*>>.each(): SequenceEach<A> = SequenceEach()

/**
 * [FilterIndex] instance definition for [SequenceK].
 */
@extension
interface SequenceFilterIndex<A> : FilterIndex<Sequence<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<Sequence<A>, A> =
    Traversal { s, f ->
      s.mapIndexed { index, a -> a toT index }.k().map { (a, j) ->
        if (p(j)) f(a) else a
      }
    }

  companion object {
    operator fun <A> invoke(): SequenceFilterIndex<A> = object : SequenceFilterIndex<A> {}
  }
}

fun <A> KClass<Sequence<*>>.filterIndex(): SequenceFilterIndex<A> = SequenceFilterIndex()

/**
 * [Index] instance definition for [SequenceK].
 */
@extension
interface SequenceIndex<A> : Index<Sequence<A>, Int, A> {
  override fun index(i: Int): Optional<Sequence<A>, A> = POptional(
    getOrModify = { it.elementAtOrNull(i)?.right() ?: it.left() },
    set = { s, a -> s.mapIndexed { index, aa -> if (index == i) a else aa } }
  )

  companion object {
    operator fun <A> invoke(): Index<Sequence<A>, Int, A> = object : SequenceIndex<A> {}
  }
}

fun <A> KClass<Sequence<*>>.index(): Index<Sequence<A>, Int, A> = SequenceIndex()

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
