package arrow.optics

import arrow.Kind
import arrow.core.Const
import arrow.core.Either
import arrow.core.ListK
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.identity
import arrow.typeclasses.Foldable
import arrow.typeclasses.Monoid

/**
 * A [Fold] is an optic that allows to focus into structure and get multiple results.
 *
 * [Fold] is a generalisation of an instance of [Foldable] and is implemented in terms of foldMap.
 *
 * @param S the source of a [Fold]
 * @param A the target of a [Fold]
 */
interface Fold<S, A> {

  @Deprecated("This method is deprecated, and will be removed in 0.13.0. Please provide the empty value and combine method for R instead", replaceWith = ReplaceWith("foldMap(s: S, empty: R, combine: (R, R) -> R, map: (A) -> R)"))
  fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R =
    foldMap(s, M.empty(), { a, b -> M.run { a.combine(b) } }, f)

  /**
   * Map each target to a type R and use a Monoid to fold the results
   */
  fun <R> foldMap(s: S, empty: R, combine: (R, R) -> R, map: (A) -> R): R

  companion object {

    fun <A> id() = PIso.id<A>().asFold()

    /**
     * [Fold] that takes either [S] or [S] and strips the choice of [S].
     */
    fun <S, R> codiagonal() = object : Fold<Either<S, S>, S> {
  override fun <R> foldMap(s: Either<S, S>, empty: R, combine: (R, R) -> R, map: (S) -> R): R =
    s.fold(map, map)
    }

    /**
     * Creates a [Fold] based on a predicate of the source [S]
     */
    fun <S> select(p: (S) -> Boolean): Fold<S, S> = object : Fold<S, S> {
      override fun <R> foldMap(s: S, empty: R, combine: (R, R) -> R, map: (S) -> R): R =
        if (p(s)) map(s) else empty
    }

    /**
     * [Fold] that points to nothing
     */
    fun <A, B> void() = POptional.void<A, B>().asFold()

    /**
     * Create a [Fold] from a [arrow.typeclasses.Foldable]
     */
    @Deprecated("This method is deprecated, and will be removed in 0.13.0. Please use the provided method for types that extend Iterable")
    fun <F, S> fromFoldable(foldable: Foldable<F>) = object : Fold<Kind<F, S>, S> {
      override fun <R> foldMap(s: Kind<F, S>, empty: R, combine: (R, R) -> R, map: (S) -> R): R =
        foldable.run { s.foldMap(object : Monoid<R> {
          override fun empty(): R = empty
          override fun R.combine(b: R): R = combine(this, b)
        }, map) }
    }

    /**
     * Create a [Fold] for a type that extends [kotlin.collections.Iterable]
     */
    operator fun <S : Iterable<A>, A> invoke(): Fold<S, A> = object : Fold<S, A> {
      override fun <R> foldMap(s: S, empty: R, combine: (R, R) -> R, map: (A) -> R): R =
        s.map(map).fold(empty, combine)
    }
  }

  /**
   * Calculate the number of targets
   */
  fun size(s: S) = foldMap(s, 0, Int::plus, { 1 })

  /**
   * Check if all targets satisfy the predicate
   */
  fun forall(s: S, p: (A) -> Boolean): Boolean = foldMap(s, true, Boolean::and, p)

  /**
   * Check if there is no target
   */
  fun isEmpty(s: S): Boolean = size(s) == 0

  /**
   * Check if there is at least one target
   */
  fun nonEmpty(s: S): Boolean = !isEmpty(s)

  /**
   * Get the first target
   */
  fun headOption(s: S): Option<A> = foldMap(
    s,
    Const(None),
    { acc, a ->
      if (acc.value().isDefined()) acc else a
    },
    { Const<Option<A>, Unit>(Some(it)) }
  ).value()

  /**
   * Get the last target
   */
  fun lastOption(s: S): Option<A> = foldMap(
    s,
    Const(None),
    { acc, a ->
      if (a.value().isDefined()) a else acc
    },
    { Const<Option<A>, Unit>(Some(it)) }
  ).value()

  /**
   * Fold using the given [Monoid] instance.
   */
  @Deprecated("This method is deprecated, and will be removed in 0.13.0.", replaceWith = ReplaceWith("fun fold(s: S, empty: A, combine: (A, A) -> A): A"))
  fun fold(M: Monoid<A>, s: S, empty: A): A = foldMap(M, s, ::identity)

  /**
   * Fold providing empty value and combine method for A.
   */
  fun fold(s: S, empty: A, combine: (A, A) -> A): A = foldMap(s, empty, combine, ::identity)

  /**
   * Alias for fold.
   */
  @Deprecated("This method is deprecated, and will be removed in 0.13.0.", replaceWith = ReplaceWith("fun combineAll(s: S, empty: A, combine: (A, A) -> A): A"))
  fun combineAll(M: Monoid<A>, s: S): A = foldMap(M, s, ::identity)

  /**
   * Alias for fold.
   */
  fun combineAll(s: S, empty: A, combine: (A, A) -> A): A = fold(s, empty, combine)

  /**
   * Get all targets of the [Fold]
   */
  @Deprecated("This method is deprecated, and will be removed in 0.13.0.")
  fun getAll(s: S): ListK<A> = foldMap(ListK.monoid(), s) { ListK.just(it) }

  /**
   * Join two [Fold] with the same target
   */
  infix fun <C> choice(other: Fold<C, A>): Fold<Either<S, C>, A> = object : Fold<Either<S, C>, A> {
    override fun <R> foldMap(s: Either<S, C>, empty: R, combine: (R, R) -> R, map: (A) -> R): R =
      s.fold(
        { l -> this@Fold.foldMap(l, empty, combine, map) },
        { r -> other.foldMap(r, empty, combine, map)}
      )
  }

  /**
   * Create a sum of the [Fold] and a type [C]
   */
  fun <C> left(): Fold<Either<S, C>, Either<A, C>> = object : Fold<Either<S, C>, Either<A, C>> {
    override fun <R> foldMap(s: Either<S, C>, empty: R, combine: (R, R) -> R, map: (Either<A, C>) -> R): R =
      s.fold(
        { l -> this@Fold.foldMap(l, empty, combine) { b -> map(Either.left(b)) } },
        { r -> map(Either.right(r))}
      )
  }

  /**
   * Create a sum of a type [C] and the [Fold]
   */
  fun <C> right(): Fold<Either<C, S>, Either<C, A>> = object : Fold<Either<C, S>, Either<C, A>> {
    override fun <R> foldMap(s: Either<C, S>, empty: R, combine: (R, R) -> R, map: (Either<C, A>) -> R): R =
      s.fold(
        { l -> map(Either.left(l)) },
        { r -> this@Fold.foldMap(r, empty, combine) { b -> map(Either.right(b)) } }
      )
  }

  /**
   * Compose a [Fold] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = object : Fold<S, C> {
    override fun <R> foldMap(s: S, empty: R, combine: (R, R) -> R, map: (C) -> R): R =
      this@Fold.foldMap(s, empty, combine) { c ->
        other.foldMap(c, empty, combine, map)
      }
  }

  /**
   * Compose a [Fold] with a [Getter]
   */
  infix fun <C> compose(other: Getter<A, C>): Fold<S, C> = compose(other.asFold())

  /**
   * Compose a [Fold] with a [Optional]
   */
  infix fun <C> compose(other: Optional<A, C>): Fold<S, C> = compose(other.asFold())

  /**
   * Compose a [Fold] with a [Prism]
   */
  infix fun <C> compose(other: Prism<A, C>): Fold<S, C> = compose(other.asFold())

  /**
   * Compose a [Fold] with a [Lens]
   */
  infix fun <C> compose(other: Lens<A, C>): Fold<S, C> = compose(other.asFold())

  /**
   * Compose a [Fold] with a [Iso]
   */
  infix fun <C> compose(other: Iso<A, C>): Fold<S, C> = compose(other.asFold())

  /**
   * Compose a [Fold] with a [Traversal]
   */
  infix fun <C> compose(other: Traversal<A, C>): Fold<S, C> = compose(other.asFold())

  /**
   * Plus operator  overload to compose lenses
   */
  operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Optional<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Getter<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Prism<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Lens<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Iso<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Traversal<A, C>): Fold<S, C> = compose(other)

  /**
   * Find the first element matching the predicate, if one exists.
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> =
    foldMap(
      s,
      Const(None),
      { acc, a -> if (acc.value().isDefined()) acc else a }
    ) { b ->
      if (p(b)) Const<Option<A>, Unit>(Some(b)) else Const(None)
    }.value()

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  fun exists(s: S, p: (A) -> Boolean): Boolean = find(s, p).fold({ false }, { true })
}
