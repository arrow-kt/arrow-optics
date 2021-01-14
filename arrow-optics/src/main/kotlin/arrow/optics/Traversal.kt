package arrow.optics

import arrow.Kind
import arrow.core.Either

@Deprecated(KindDeprecation)
class ForPTraversal private constructor() {
  companion object
}
@Deprecated(KindDeprecation)
typealias PTraversalOf<S, T, A, B> = arrow.Kind4<ForPTraversal, S, T, A, B>
@Deprecated(KindDeprecation)
typealias PTraversalPartialOf<S, T, A> = arrow.Kind3<ForPTraversal, S, T, A>
@Deprecated(KindDeprecation)
typealias PTraversalKindedJ<S, T, A, B> = arrow.HkJ4<ForPTraversal, S, T, A, B>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(KindDeprecation)
inline fun <S, T, A, B> PTraversalOf<S, T, A, B>.fix(): PTraversal<S, T, A, B> =
  this as PTraversal<S, T, A, B>

/**
 * [Traversal] is a type alias for [PTraversal] which fixes the type arguments
 * and restricts the [PTraversal] to monomorphic updates.
 */
typealias Traversal<S, A> = PTraversal<S, S, A, A>

typealias ForTraversal = ForPTraversal
typealias TraversalOf<S, A> = PTraversalOf<S, S, A, A>
typealias TraversalPartialOf<S> = Kind<ForTraversal, S>
typealias TraversalKindedJ<S, A> = PTraversalKindedJ<S, S, A, A>

/**
 * A [Traversal] is an optic that allows to see into a structure with 0 to N foci.
 *
 * [Traversal] is a generalisation of [arrow.Traverse] and can be seen as a representation of modifyF.
 * all methods are written in terms of modifyF
 *
 * @param S the source of a [PTraversal]
 * @param T the modified source of a [PTraversal]
 * @param A the target of a [PTraversal]
 * @param B the modified target of a [PTraversal]
 */
fun interface PTraversal<S, T, A, B> : PTraversalOf<S, T, A, B> {

  fun map(s: S, f: (A) -> B): T

  /**
   * Set polymorphically the target of a [PTraversal] with a value
   */
  fun set(s: S, b: B): T = modify(s) { b }

  /**
   * Modify polymorphically the target of a [PTraversal] with a function [f]
   */
  fun modify(s: S, f: (A) -> B): T = map(s, f)

  companion object {
    fun <S> id() = PIso.id<S>().asTraversal()

    fun <S> codiagonal(): Traversal<Either<S, S>, S> =
      Traversal { s, f -> s.bimap(f, f) }

    /**
     * [PTraversal] that points to nothing
     */
    fun <S, A> void() = POptional.void<S, A>().asTraversal()

    /**
     * [PTraversal] constructor from multiple getters of the same source.
     */
    operator fun <S, T, A, B> invoke(get1: (S) -> A, get2: (S) -> A, set: (B, B, S) -> T): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      set: (B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      set: (B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      set: (B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      set: (B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      set: (B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      set: (B, B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      get9: (S) -> A,
      set: (B, B, B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      get9: (S) -> A,
      get10: (S) -> A,
      set: (B, B, B, B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s)), f(get10(s)), s) }
  }

  fun <U, V> choice(other: PTraversal<U, V, A, B>): PTraversal<Either<S, U>, Either<T, V>, A, B> =
    PTraversal { s, f ->
      s.fold(
        { a -> Either.Left(this@PTraversal.modify(a, f)) },
        { u -> Either.Right(other.map(u, f)) }
      )
    }

  /**
   * Compose a [PTraversal] with a [PTraversal]
   */
  infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> =
    PTraversal { s, f ->
      this@PTraversal.map(s) { b -> other.modify(b, f) }
    }

  /**
   * Compose a [PTraversal] with a [PEvery]
   */
  infix fun <C, D> compose(other: PEvery<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [PSetter]
   */
  infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

  /**
   * Compose a [PTraversal] with a [POptional]
   */
  infix fun <C, D> compose(other: POptional<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [PLens]
   */
  infix fun <C, D> compose(other: PLens<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [PPrism]
   */
  infix fun <C, D> compose(other: PPrism<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [PIso]
   */
  infix fun <C, D> compose(other: PIso<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Plus operator overload to compose [PTraversal] with other optics
   */
  operator fun <C, D> plus(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: POptional<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PLens<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PPrism<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PIso<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  fun asSetter(): PSetter<S, T, A, B> = PSetter { s, f -> modify(s, f) }
}
