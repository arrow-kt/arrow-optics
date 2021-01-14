package arrow.optics

import arrow.core.Const
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.boolean
import arrow.core.extensions.int
import arrow.core.identity
import arrow.typeclasses.Monoid
import kotlin.collections.plus as _plus

typealias Every<S, A> = PEvery<S, S, A, A>

/**
 * Composition of Fold and Traversal
 * It combines their powers
 */
interface PEvery<S, T, A, B> : PTraversal<S, T, A, B> {

  /**
   * Map each target to a type R and use a Monoid to fold the results
   */
  fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R

  override fun map(s: S, f: (A) -> B): T

  /**
   * Set polymorphically the target of a [PTraversal] with a value
   */
  override fun set(s: S, b: B): T = modify(s) { b }

  /**
   * Modify polymorphically the target of a [PTraversal] with a function [f]
   */
  override fun modify(s: S, f: (A) -> B): T = map(s, f)

  /**
   * Calculate the number of targets
   */
  fun size(s: S) = foldMap(Monoid.int(), s = s, f = { _ -> 1 })

  /**
   * Check if all targets satisfy the predicate
   */
  fun forall(s: S, p: (A) -> Boolean): Boolean = foldMap(Monoid.boolean(), s, p)

  /**
   * Check if there is no target
   */
  fun isEmpty(s: S): Boolean = foldMap(Monoid.boolean(), s) { _ -> false }

  /**
   * Check if there is at least one target
   */
  fun nonEmpty(s: S): Boolean = !isEmpty(s)

  /**
   * Find the first element matching the predicate, if one exists.
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> =
    foldMap(firstOptionMonoid<A>(), s) { b -> (if (p(b)) Const(Some(b)) else Const(None)) }.value()

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  fun exists(s: S, p: (A) -> Boolean): Boolean = find(s, p).fold({ false }, { true })

  /**
   * Get the first target
   */
  @Deprecated("Use firstOrNull")
  fun headOption(s: S): Option<A> = foldMap(firstOptionMonoid<A>(), s) { b -> Const(Some(b)) }.value()

  fun firstOrNull(s: S): A? = headOption(s).orNull()

  /**
   * Get the last target
   */
  @Deprecated("Use lastOption")
  fun lastOption(s: S): Option<A> = foldMap(lastOptionMonoid<A>(), s) { b -> Const(Some(b)) }.value()

  fun lastOrNull(s: S): A? = lastOption(s).orNull()

  // Add additional operations like `tail` etc which return `List`

  /**
   * Fold using the given [Monoid] instance.
   */
  fun fold(M: Monoid<A>, s: S): A = foldMap(M, s, ::identity)

  /**
   * Alias for fold.
   */
  fun combineAll(M: Monoid<A>, s: S): A = foldMap(M, s, ::identity)

  /**
   * Get all targets of the [Fold]
   */
  fun getAll(s: S): List<A> = foldMap(Monoid.list(), s) { listOf(it) }

  fun asTraversal(): PTraversal<S, T, A, B> =
    PTraversal { s, f -> map(s, f) }

  fun asFold(): Fold<S, A> = object : Fold<S, A> {
    override fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R =
      this@PEvery.foldMap(M, s, f)
  }

  /**
   * Compose a [PEvery] with a [PEvery]
   */
  override infix fun <C, D> compose(other: PEvery<A, B, C, D>): PEvery<S, T, C, D> = object : PEvery<S, T, C, D> {
    override fun <R> foldMap(M: Monoid<R>, s: S, f: (C) -> R): R =
      this@PEvery.foldMap(M, s) { c -> other.foldMap(M, c, f) }

    override fun map(s: S, f: (C) -> D): T =
      this@PEvery.map(s) { b -> other.modify(b, f) }
  }

  /**
   * Compose a [PTraversal] with a [PTraversal]
   */
  override infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> =
    PTraversal { s, f -> this@PEvery.map(s) { b -> other.modify(b, f) } }

  /**
   * Compose a [Fold] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = object : Fold<S, C> {
    override fun <R> foldMap(M: Monoid<R>, s: S, f: (C) -> R): R =
      this@PEvery.foldMap(M, s) { c -> other.foldMap(M, c, f) }
  }

  /**
   * Compose a [PTraversal] with a [PSetter]
   */
  override infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

  /**
   * Compose a [PTraversal] with a [POptional]
   */
  override infix fun <C, D> compose(other: POptional<A, B, C, D>): PEvery<S, T, C, D> = compose(other.asEvery())

  /**
   * Compose a [PTraversal] with a [PLens]
   */
  override infix fun <C, D> compose(other: PLens<A, B, C, D>): PEvery<S, T, C, D> = compose(other.asEvery())

  /**
   * Compose a [PTraversal] with a [PPrism]
   */
  override infix fun <C, D> compose(other: PPrism<A, B, C, D>): PEvery<S, T, C, D> = compose(other.asEvery())

  /**
   * Compose a [PTraversal] with a [PIso]
   */
  override infix fun <C, D> compose(other: PIso<A, B, C, D>): PEvery<S, T, C, D> = compose(other.asEvery())

  override fun asSetter(): PSetter<S, T, A, B> = PSetter { s, f -> modify(s, f) }

  companion object {
    fun <S, A> from(T: Traversal<S, A>, F: Fold<S, A>): Every<S, A> = object : Every<S, A> {
      override fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R = F.foldMap(M, s, f)
      override fun map(s: S, f: (A) -> A): S = T.map(s, f)
    }
  }

  /**
   * DSL to compose [Traversal] with a [Lens] for a structure [S] to see all its foci [A]
   *
   * @receiver [Lens] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PLens<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this@every.compose(this@PEvery)

  /**
   * DSL to compose [Traversal] with a [Iso] for a structure [S] to see all its foci [A]
   *
   * @receiver [Iso] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PIso<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this@every.compose(this@PEvery)

  /**
   * DSL to compose [Traversal] with a [Prism] for a structure [S] to see all its foci [A]
   *
   * @receiver [Prism] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PPrism<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this.compose(this@PEvery)

  /**
   * DSL to compose [Traversal] with a [Optional] for a structure [S] to see all its foci [A]
   *
   * @receiver [Optional] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> POptional<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this.compose(this@PEvery)

  /**
   * DSL to compose [Traversal] with a [Setter] for a structure [S] to see all its foci [A]
   *
   * @receiver [Setter] with a focus in [S]
   * @return [Setter] with a focus in [A]
   */
  val <U, V> PSetter<U, V, S, T>.every: PSetter<U, V, A, B>
    get() = this.compose(this@PEvery)

  /**
   * DSL to compose [Traversal] with a [Traversal] for a structure [S] to see all its foci [A]
   *
   * @receiver [Traversal] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PTraversal<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() = this.compose(this@PEvery)
}

fun <A> Monoid.Companion.list(): Monoid<List<A>> =
  ListMonoid as Monoid<List<A>>

object ListMonoid : Monoid<List<Any?>> {
  override fun empty(): List<Any?> = emptyList()
  override fun List<Any?>.combine(b: List<Any?>): List<Any?> = this._plus(b)
}
