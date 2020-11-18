package arrow.optics

import arrow.core.Const
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.toOption
import arrow.typeclasses.Foldable

/**
 * A [Fold] is an optic that allows to focus into structure and get multiple results.
 *
 * [Fold] is a generalisation of an instance of [Foldable] and is implemented in terms of foldMap.
 *
 * @param S the source of a [Fold]
 * @param A the target of a [Fold]
 */
fun interface Fold<S : Iterable<A>, A, R> {

  /**
   * Map each target to a type R and use a Monoid to fold the results
   */
  fun S.foldMap(empty: R, plus: (R, R) -> R, f: (A) -> R): R

  companion object {

    fun <A> id() =
      PIso.id<A>().asFold()

    /**
     * TODO() Make Either iterable with cross PR to core
     * [Fold] that takes either [S] or [S] and strips the choice of [S].
     */
//    fun <S, R> codiagonal(): Fold<Either<S, S>, S, R> =
//      Fold { _, _, f -> fold(f, f) }

    /**
     * Creates a [Fold] based on a predicate of the source [S]
     */
    fun <S : Iterable<S>, R> select(p: (S) -> Boolean): Fold<S, S, R> =
      Fold { empty, _, f -> if (p(this)) f(this) else empty }

    /**
     * [Fold] that points to nothing
     */
    fun <A, B> void() =
      POptional.void<A, B>().asFold()

  }

  /**
   * Calculate the number of targets
   */
  fun size(s: S): Int =
    s.fold(0) { acc, _ -> acc + 1 }

  /**
   * Check if all targets satisfy the predicate
   */
  fun forall(s: S, p: (A) -> Boolean): Boolean =
    s.all(p)

  /**
   * Check if there is no target
   */
  fun isEmpty(s: S): Boolean =
    size(s) > 0

  /**
   * Check if there is at least one target
   */
  fun nonEmpty(s: S): Boolean =
    !isEmpty(s)

  /**
   * Get the first target
   */
  fun headOption(s: S): Option<A> =
    s.firstOrNull().toOption()

  /**
   * Get the last target
   */
  fun lastOption(s: S): Option<A> =
    s.lastOrNull().toOption()

  /**
   * Fold using the given [Monoid] instance.
   */
  fun fold(empty: A, plus: (A, A) -> A, s: S): A =
    s.fold(empty) { acc, a -> plus(acc, a) }

  /**
   * Alias for fold.
   */
  fun combineAll(empty: A, plus: (A, A) -> A, s: S): A =
    s.fold(empty) { acc, a -> plus(acc, a) }

  /**
   * Get all targets of the [Fold]
   */
  fun getAll(s: S): List<A> =
    s.toList()

  /**
   * Join two [Fold] with the same target
   */
//  TODO() Make Either iterable with cross PR to core
//
//  infix fun <C : Iterable<A>> choice(other: Fold<C, A, R>): Fold<Either<S, C>, A, R> =
//    Fold { empty, plus, f ->
//      fold({ ac -> ac.foldMap(empty, plus, f) }, { c -> other.run { c.foldMap(empty, plus, f) } })
//    }

  /**
   * TODO() Make Either iterable with cross PR to core
   * Create a sum of the [Fold] and a type [C]
   */
//  fun <C> left(): Fold<Either<S, C>, Either<A, C>> = object : Fold<Either<S, C>, Either<A, C>> {
//    override fun <R> Either<S, C>.foldMap(empty: R, f: (Either<A, C>) -> R): R =
//      fold({ a1: S -> this@Fold.foldMap(empty, a1) { b -> f(Either.Left(b)) } }, { c -> f(Either.Right(c)) })
//  }

  /**
   * TODO() Make Either iterable with cross PR to core
   * Create a sum of a type [C] and the [Fold]
   */
//  fun <C> right(): Fold<Either<C, S>, Either<C, A>> = object : Fold<Either<C, S>, Either<C, A>> {
//    override fun <R> Either<C, S>.foldMap(empty: R, f: (Either<C, A>) -> R): R =
//      fold({ c -> f(Either.Left(c)) }, { a1 -> this@Fold.foldMap(empty, a1) { b -> f(Either.Right(b)) } })
//  }

  /**
   * Find the first element matching the predicate, if one exists.
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> =
    s.find(p).toOption()

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  fun exists(s: S, p: (A) -> Boolean): Boolean =
    s.any(p)
}

/**
 * Compose a [Fold] with a [Fold]
 */
infix fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.compose(other: Fold<A, C, R>): Fold<S, C, R> =
  Fold { plus, empty, f ->
    foldMap(plus, empty) { c -> other.run { c.foldMap(plus, empty, f) } }
  }

/**
 * Compose a [Fold] with a [Getter]
 */
infix fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.compose(other: Getter<A, C>): Fold<S, C, R> =
  compose(other.asFold())

/**
 * Compose a [Fold] with a [Optional]
 */
infix fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.compose(other: Optional<A, C>): Fold<S, C, R> =
  compose(other.asFold())

/**
 * Compose a [Fold] with a [Prism]
 */
infix fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.compose(other: Prism<A, C>): Fold<S, C, R> =
  compose(other.asFold())

/**
 * Compose a [Fold] with a [Lens]
 */
infix fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.compose(other: Lens<A, C>): Fold<S, C, R> =
  compose(other.asFold())

/**
 * Compose a [Fold] with a [Iso]
 */
infix fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.compose(other: Iso<A, C>): Fold<S, C, R> =
  compose(other.asFold())

/**
 * Compose a [Fold] with a [Traversal]
 */
infix fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.compose(other: Traversal<A, C>): Fold<S, C, R> =
  compose(other.asFold())

/**
 * Plus operator  overload to compose lenses
 */
operator fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.plus(other: Fold<A, C, R>): Fold<S, C, R> =
  compose(other)

operator fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.plus(other: Optional<A, C>): Fold<S, C, R> =
  compose(other)

operator fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.plus(other: Getter<A, C>): Fold<S, C, R> =
  compose(other)

operator fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.plus(other: Prism<A, C>): Fold<S, C, R> =
  compose(other)

operator fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.plus(other: Lens<A, C>): Fold<S, C, R> =
  compose(other)

operator fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.plus(other: Iso<A, C>): Fold<S, C, R> =
  compose(other)

operator fun <S : Iterable<C>, A : Iterable<C>, R, C : A> Fold<S, A, R>.plus(other: Traversal<A, C>): Fold<S, C, R> =
  compose(other)
