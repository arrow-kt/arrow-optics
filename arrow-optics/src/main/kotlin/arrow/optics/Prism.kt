package arrow.optics

import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.compose
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.toT
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid

/**
 * [Prism] is a type alias for [PPrism] which fixes the type arguments
 * and restricts the [PPrism] to monomorphic updates.
 */
typealias Prism<S, A> = PPrism<S, S, A, A>

/**
 * A [Prism] is a loss less invertible optic that can look into a structure and optionally find its focus.
 * Mostly used for finding a focus that is only present under certain conditions i.e. list head Prism<List<Int>, Int>
 *
 * A (polymorphic) [PPrism] is useful when setting or modifying a value for a polymorphic sum type
 * i.e. PPrism<Option<String>, Option<Int>, String, Int>
 *
 * A [PPrism] gathers the two concepts of pattern matching and constructor and thus can be seen as a pair of functions:
 * - `getOrModify: A -> Either<A, B>` meaning it returns the focus of a [PPrism] OR the original value
 * - `reverseGet : B -> A` meaning we can construct the source type of a [PPrism] from a focus `B`
 *
 * @param S the source of a [PPrism]
 * @param T the modified source of a [PPrism]
 * @param A the focus of a [PPrism]
 * @param B the modified focus of a [PPrism]
 */
interface PPrism<S, T, A, B> : POptional<S, T, A, B>, PSetter<S, T, A, B>, Fold<S, A>, PTraversal<S, T, A, B>, PEvery<S, T, A, B> {

  override fun getOrModify(source: S): Either<T, A>

  fun reverseGet(focus: B): T

  override fun <R> foldMap(M: Monoid<R>, source: S, f: (A) -> R): R =
    getOption(source).map(f).getOrElse(M::empty)

  /**
   * Modify the focus of a [PPrism] with a function
   */
  override fun modify(source: S, f: (A) -> B): T =
    getOrModify(source).fold(::identity) { a -> reverseGet(f(a)) }

  /**
   * Get the focus or [Option.None] if focus cannot be seen
   */
  override fun getOption(source: S): Option<A> =
    getOrModify(source).toOption()

  /**
   * Set the focus of a [PPrism] with a value
   */
  override fun set(source: S, focus: B): T =
    modify(source) { focus }

  /**
   * Set the focus of a [PPrism] with a value
   */
  override fun setOption(source: S, b: B): Option<T> =
    modifyOption(source) { b }

  /**
   * Lift a function [f]: `(A) -> B to the context of `S`: `(S) -> Option<T>`
   */
  fun liftOption(f: (A) -> B): (S) -> Option<T> =
    { s -> getOption(s).map { b -> reverseGet(f(b)) } }

  /**
   * Create a product of the [PPrism] and a type [C]
   */
  override fun <C> first(): PPrism<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> = PPrism(
    { (s, c) -> getOrModify(s).bimap({ it toT c }, { it toT c }) },
    { (b, c) -> reverseGet(b) toT c }
  )

  /**
   * Create a product of a type [C] and the [PPrism]
   */
  override fun <C> second(): PPrism<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> = PPrism(
    { (c, s) -> getOrModify(s).bimap({ c toT it }, { c toT it }) },
    { (c, b) -> c toT reverseGet(b) }
  )

  /**
   * Create a sum of the [PPrism] and a type [C]
   */
  override fun <C> left(): PPrism<Either<S, C>, Either<T, C>, Either<A, C>, Either<B, C>> = PPrism(
    { it.fold({ a -> getOrModify(a).bimap({ Either.Left(it) }, { Either.Left(it) }) }, { c -> Either.Right(Either.Right(c)) }) },
    {
      when (it) {
        is Either.Left -> Either.Left(reverseGet(it.a))
        is Either.Right -> Either.Right(it.b)
      }
    }
  )

  /**
   * Create a sum of a type [C] and the [PPrism]
   */
  override fun <C> right(): PPrism<Either<C, S>, Either<C, T>, Either<C, A>, Either<C, B>> = PPrism(
    { it.fold({ c -> Either.Right(Either.Left(c)) }, { s -> getOrModify(s).bimap({ Either.Right(it) }, { Either.Right(it) }) }) },
    { it.map(this::reverseGet) }
  )

  /**
   * Compose a [PPrism] with another [PPrism]
   */
  infix fun <C, D> compose(other: PPrism<A, B, C, D>): PPrism<S, T, C, D> = PPrism(
    getOrModify = { s -> getOrModify(s).flatMap { a -> other.getOrModify(a).bimap({ set(s, it) }, ::identity) } },
    reverseGet = this::reverseGet compose other::reverseGet
  )

  operator fun <C, D> plus(other: PPrism<A, B, C, D>): PPrism<S, T, C, D> =
    this compose other

  companion object {

    fun <S> id(): PPrism<S, S, S, S> =
      PIso.id()

    /**
     * Invoke operator overload to create a [PPrism] of type `S` with focus `A`.
     * Can also be used to construct [Prism]
     */
    operator fun <S, T, A, B> invoke(getOrModify: (S) -> Either<T, A>, reverseGet: (B) -> T) =
      object : PPrism<S, T, A, B> {
        override fun getOrModify(source: S): Either<T, A> = getOrModify(source)
        override fun reverseGet(focus: B): T = reverseGet(focus)
      }

    /**
     * A [PPrism] that checks for equality with a given value [a]
     */
    fun <A> only(a: A, EQA: Eq<A>): Prism<A, Unit> = Prism(
      getOrModify = { a2 -> (if (EQA.run { a.eqv(a2) }) Either.Left(a) else Either.Right(Unit)) },
      reverseGet = { a }
    )
  }
}

/**
 * Invoke operator overload to create a [PPrism] of type `S` with a focus `A` where `A` is a subtype of `S`
 * Can also be used to construct [Prism]
 */
@Suppress("FunctionName")
fun <S, A> Prism(getOption: (S) -> Option<A>, reverseGet: (A) -> S): Prism<S, A> = Prism(
  getOrModify = { getOption(it).toEither { it } },
  reverseGet = { reverseGet(it) }
)
