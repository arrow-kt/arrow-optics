package arrow.optics

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.toT
import arrow.typeclasses.Monoid

/**
 * [Lens] is a type alias for [PLens] which fixes the type arguments
 * and restricts the [PLens] to monomorphic updates.
 */
typealias Lens<S, A> = PLens<S, S, A, A>

/**
 * A [Lens] (or Functional Reference) is an optic that can focus into a structure for
 * getting, setting or modifying the focus (target).
 *
 * A (polymorphic) [PLens] is useful when setting or modifying a value for a constructed type
 * i.e. PLens<Tuple2<Double, Int>, Tuple2<String, Int>, Double, String>
 *
 * A [PLens] can be seen as a pair of functions:
 * - `get: (S) -> A` meaning we can focus into an `S` and extract an `A`
 * - `set: (B) -> (S) -> T` meaning we can focus into an `S` and set a value `B` for a target `A` and obtain a modified source `T`
 *
 * @param S the source of a [PLens]
 * @param T the modified source of a [PLens]
 * @param A the focus of a [PLens]
 * @param B the modified focus of a [PLens]
 */
interface PLens<S, T, A, B> : Getter<S, A>, POptional<S, T, A, B>, PSetter<S, T, A, B>, Fold<S, A>, PTraversal<S, T, A, B>, PEvery<S, T, A, B> {

  override fun get(source: S): A

  override fun set(source: S, focus: B): T

  override fun getOrModify(source: S): Either<T, A> =
    Either.Right(get(source))

  override fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: A) -> R): R =
    map(get(source))

  /**
   * Join two [PLens] with the same focus in [A]
   */
  infix fun <S1, T1> choice(other: PLens<S1, T1, A, B>): PLens<Either<S, S1>, Either<T, T1>, A, B> = PLens(
    { ss -> ss.fold(this::get, other::get) },
    { ss, b -> ss.bimap({ s -> set(s, b) }, { s -> other.set(s, b) }) }
  )

  /**
   * Pair two disjoint [PLens]
   */
  infix fun <S1, T1, A1, B1> split(other: PLens<S1, T1, A1, B1>): PLens<Tuple2<S, S1>, Tuple2<T, T1>, Tuple2<A, A1>, Tuple2<B, B1>> =
    PLens(
      { (s, c) -> get(s) toT other.get(c) },
      { (s, s1), (b, b1) -> set(s, b) toT other.set(s1, b1) }
    )

  /**
   * Create a product of the [PLens] and a type [C]
   */
  override fun <C> first(): PLens<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> = PLens(
    { (s, c) -> get(s) toT c },
    { (s, _), (b, c) -> set(s, b) toT c }
  )

  /**
   * Create a product of a type [C] and the [PLens]
   */
  override fun <C> second(): PLens<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> = PLens(
    { (c, s) -> c toT get(s) },
    { (_, s), (c, b) -> c toT set(s, b) }
  )

  /**
   * Compose a [PLens] with another [PLens]
   */
  infix fun <C, D> compose(other: PLens<A, B, C, D>): PLens<S, T, C, D> = Lens(
    { a -> other.get(get(a)) },
    { s, c -> set(s, other.set(get(s), c)) }
  )

  operator fun <C, D> plus(other: PLens<A, B, C, D>): PLens<S, T, C, D> =
    this compose other

  companion object {

    fun <S> id(): PLens<S, S, S, S> =
      PIso.id()

    /**
     * [PLens] that takes either [S] or [S] and strips the choice of [S].
     */
    fun <S> codiagonal(): Lens<Either<S, S>, S> =
      Lens(
        get = { it.fold(::identity, ::identity) },
        set = { s, b -> s.bimap({ b }, { b }) }
      )

    /**
     * Invoke operator overload to create a [PLens] of type `S` with target `A`.
     * Can also be used to construct [Lens]
     */
    operator fun <S, T, A, B> invoke(get: (S) -> A, set: (S, B) -> T) =
      object : PLens<S, T, A, B> {
        override fun get(s: S): A = get(s)
        override fun set(source: S, focus: B): T = set(source, focus)
      }
  }
}
