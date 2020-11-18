package arrow.optics

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.compose
import arrow.core.identity
import arrow.core.toT

/**
 * A [Getter] is an optic that allows to see into a structure and getting a focus.
 *
 * A [Getter] can be seen as a get function:
 * - `get: (S) -> A` meaning we can look into an `S` and get an `A`
 *
 * @param S the source of a [Getter]
 * @param A the focus of a [Getter]
 */
fun interface Getter<S, A> {

  /**
   * Get the focus of a [Getter]
   */
  fun get(s: S): A

  companion object {

    fun <S> id() = PIso.id<S>().asGetter()

    /**
     * [Getter] that takes either [S] or [S] and strips the choice of [S].
     */
    fun <S> codiagonal(): Getter<Either<S, S>, S> =
      Getter { aa -> aa.fold(::identity, ::identity) }

  }

  /**
   * Join two [Getter] with the same focus
   */
  infix fun <C> choice(other: Getter<C, A>): Getter<Either<S, C>, A> = Getter { s ->
    s.fold(this::get, other::get)
  }

  /**
   * Pair two disjoint [Getter]
   */
  infix fun <C, D> split(other: Getter<C, D>): Getter<Tuple2<S, C>, Tuple2<A, D>> = Getter { (s, c) ->
    get(s) toT other.get(c)
  }

  /**
   * Zip two [Getter] optics with the same source [S]
   */
  infix fun <C> zip(other: Getter<S, C>): Getter<S, Tuple2<A, C>> = Getter { s ->
    get(s) toT other.get(s)
  }

  /**
   * Find the focus [A] if it satisfies the predicate [p].
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> = get(s).let { b ->
    if (p(b)) Some(b) else None
  }

  /**
   * Check if the focus [A] satisfies the predicate [p].
   */
  fun exist(s: S, p: (A) -> Boolean): Boolean = p(get(s))

  /**
   * Create a product of the [Getter] and a type [C]
   */
  fun <C> first(): Getter<Tuple2<S, C>, Tuple2<A, C>> = Getter { (s, c) ->
    get(s) toT c
  }

  /**
   * Create a product of type [C] and the [Getter]
   */
  fun <C> second(): Getter<Tuple2<C, S>, Tuple2<C, A>> = Getter { (c, s) ->
    c toT get(s)
  }

  /**
   * Create a sum of the [Getter] and type [C]
   */
  fun <C> left(): Getter<Either<S, C>, Either<A, C>> = Getter { sc ->
    sc.bimap(this::get, ::identity)
  }

  /**
   * Create a sum of type [C] and the [Getter]
   */
  fun <C> right(): Getter<Either<C, S>, Either<C, A>> = Getter { cs ->
    cs.map(this::get)
  }

  /**
   * Compose a [Getter] with a [Getter]
   */
  infix fun <C> compose(other: Getter<A, C>): Getter<S, C> = Getter(other::get compose this::get)

  /**
   * Compose a [Getter] with a [Lens]
   */
  infix fun <C> compose(other: Lens<A, C>): Getter<S, C> = Getter(other::get compose this::get)

  /**
   * Compose a [Getter] with a [Iso]
   */
  infix fun <C> compose(other: Iso<A, C>): Getter<S, C> = Getter(other::get compose this::get)

  /**
   * Plus operator overload to compose optionals
   */
  operator fun <C> plus(other: Getter<A, C>): Getter<S, C> = compose(other)

  operator fun <C> plus(other: Lens<A, C>): Getter<S, C> = compose(other)

  operator fun <C> plus(other: Iso<A, C>): Getter<S, C> = compose(other)

}

fun <S : Iterable<A>, A, R> Getter<S, A>.asFold(): Fold<S, A, R> =
  Fold { _, _, f -> f(get(this)) }

/**
 * Compose a [Getter] with a [Fold]
 */
infix fun <S : Iterable<C>, A : Iterable<C>, C: A, R> Getter<S, A>.compose(other: Fold<A, C, R>): Fold<S, C, R> =
  asFold<S, A, R>().compose(other)

operator fun <S : Iterable<C>, A : Iterable<C>, C: A, R> Getter<S, A>.plus(other: Fold<A, C, R>): Fold<S, C, R> =
  compose(other)
