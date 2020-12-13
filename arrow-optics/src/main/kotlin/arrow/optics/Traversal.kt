package arrow.optics

import arrow.core.Const
import arrow.core.Eval
import arrow.core.Id
import arrow.core.ListK
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.const
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.const.traverse.traverse
import arrow.core.extensions.id.traverse.traverse
import arrow.core.extensions.list.functor.mapConst
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.extensions.monoid
import arrow.core.identity
import arrow.optics.extensions.traversal
import arrow.typeclasses.Monoid

fun interface Iter<out A> {
  fun iterator(): Iterator<A>
}

sealed class Either<out E, out A> : Iter<A> {

  override fun iterator(): Iterator<A> =
    fold({ emptyList<Nothing>().iterator() }, { iterator { yield(it) } })

  fun <B> fold(ifLeft: (E) -> B, ifRight: (A) -> B): B =
    when (this) {
      is Left -> ifLeft(value)
      is Right -> ifRight(value)
    }

  fun <B> map(f: (A) -> B): Either<E, B> =
    when (this) {
      is Left -> this
      is Right -> Right(f(value))
    }

  inline fun <C, D> bimap(crossinline leftOperation: (E) -> C, crossinline rightOperation: (A) -> D): Either<C, D> =
    fold({ Left(leftOperation(it)) }, { Right(rightOperation(it)) })

}

fun <E, A, B> Either<E, A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> =
  when (this) {
    is Left -> this
    is Right -> f(value)
  }

fun <E, A, B> Either<E, A>.ap(f: Either<E, (A) -> B>): Either<E, B> =
  flatMap { a -> f.map { it(a) } }

data class Left<out E>(val value: E) : Either<E, Nothing>() {
  override fun iterator(): Iterator<Nothing> =
    emptyList<Nothing>().iterator()
}

data class Right<out R>(val value: R) : Either<Nothing, R>() {
  override fun iterator(): Iterator<R> =
    iterator { yield(value) }
}

fun <A> A.right(): Either<Nothing, A> = Right(this)

fun <E, A, B> Either<E, A>.apEval(ff: Eval<Either<E, (A) -> B>>): Eval<Either<E, B>> =
  ff.map { ap(it) }

inline fun <E, A, B> Iterable<Either<E, A>>.traverse(crossinline f: (Either<E, A>) -> Either<E, B>): Either<E, List<B>> =
  toList().foldRight<Either<E, A>, Eval<Either<E, List<B>>>>(Eval.now(Right(emptyList()))) { a, eval ->
    f(a).apEval(
      eval.map {
        it.map { xs ->
          { b: B -> listOf(b) + xs }
        }
      }
    )
  }.value()

inline fun <E, A> List<Either<E, A>>.sequence(): Either<E, List<A>> =
  traverse(::identity)




/**
 * [Traversal] is a type alias for [PTraversal] which fixes the type arguments
 * and restricts the [PTraversal] to monomorphic updates.
 */
typealias Traversal<S, A> = PTraversal<S, S, A, A>

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
fun interface PTraversal<S, T, A, B> {

  fun modify(s: S, f: (A) -> B): T

  companion object {
    fun <S> id() = PIso.id<S>().asTraversal()

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
    ): PTraversal<S, T, A, B> = PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      set: (B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), s) }

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
    ): PTraversal<S, T, A, B> = PTraversal { s, f ->
      set(
        f(get1(s)),
        f(get2(s)),
        f(get3(s)),
        f(get4(s)),
        f(get5(s)),
        f(get6(s)),
        f(get7(s)),
        f(get8(s)),
        s
      )
    }

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
    ): PTraversal<S, T, A, B> = PTraversal { s, f ->
      set(
        f(get1(s)),
        f(get2(s)),
        f(get3(s)),
        f(get4(s)),
        f(get5(s)),
        f(get6(s)),
        f(get7(s)),
        f(get8(s)),
        f(get9(s)),
        s
      )
    }

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
    ): PTraversal<S, T, A, B> = PTraversal { s, f ->
      set(
        f(get1(s)),
        f(get2(s)),
        f(get3(s)),
        f(get4(s)),
        f(get5(s)),
        f(get6(s)),
        f(get7(s)),
        f(get8(s)),
        f(get9(s)),
        f(get10(s)),
        s
      )
    }
  }

  /**
   * Map each target to a Monoid and combine the results
   */
  fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R =
    TODO()



  /**
   * Fold using the given [Monoid] instance.
   */
  fun fold(M: Monoid<A>, s: S): A = foldMap(M, s, ::identity)

  /**
   * Alias for fold.
   */
  fun combineAll(M: Monoid<A>, s: S): A = fold(M, s)

  /**
   * Get all foci of the [PTraversal]
   */
  fun getAll(s: S): ListK<A> = foldMap(ListK.monoid(), s) { ListK(listOf(it)) }

  /**
   * Set polymorphically the target of a [PTraversal] with a value
   */
  fun set(s: S, b: B): T = modify(s) { b }

  /**
   * Calculate the number of targets in the [PTraversal]
   */
  fun size(s: S): Int = foldMap(Int.monoid(), s) { 1 }

  /**
   * Check if there is no target
   */
  fun isEmpty(s: S): Boolean = foldMap(AndMonoid, s) { _ -> false }

  /**
   * Check if there is at least one target
   */
  fun nonEmpty(s: S): Boolean = !isEmpty(s)

  /**
   * Find the first target or [Option.None] if no targets
   */
  fun headOption(s: S): Option<A> = foldMap(firstOptionMonoid<A>(), s) { b -> Some(b) }

  /**
   * Find the first target or [Option.None] if no targets
   */
  fun lastOption(s: S): Option<A> = foldMap(lastOptionMonoid<A>(), s) { b -> Some(b) }

  fun <U, V> choice(other: PTraversal<U, V, A, B>): PTraversal<Either<S, U>, Either<T, V>, A, B> =
    PTraversal { s, f ->
      s.fold(
        { a -> Left(this@PTraversal.modify(a, f)) },
        { u -> Right(other.modify(u, f)) }
      )
    }

  /**
   * Compose a [PTraversal] with a [PTraversal]
   */
  infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> =
    PTraversal { s, f -> this@PTraversal.modify(s) { b -> other.modify(b, f) } }

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
   * Compose a [PTraversal] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = asFold() compose other

  /**
   * Plus operator overload to compose [PTraversal] with other optics
   */
  operator fun <C, D> plus(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: POptional<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PLens<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PPrism<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PIso<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = compose(other)

  fun asSetter(): PSetter<S, T, A, B> = PSetter { s, f -> modify(s, f) }

  fun asFold(): Fold<S, A> = object : Fold<S, A> {
    // TODO: Temporary while Traversal is not refactored
    override fun <R> foldMap(s: S, empty: R, combine: (R, R) -> R, map: (A) -> R): R =
      this@PTraversal.foldMap(
        object : Monoid<R> {
          override fun empty(): R =
            empty

          override fun R.combine(b: R): R =
            combine(this, b)
        },
        s,
        map
      )
  }

  /**
   * Find the first target matching the predicate
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> = foldMap(firstOptionMonoid<A>(), s) { a ->
    if (p(a)) Some(a)
    else None
  }

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  fun exist(s: S, p: (A) -> Boolean): Boolean = find(s, p).fold({ false }, { true })

  /**
   * Check if forall targets satisfy the predicate
   */
  fun forall(s: S, p: (A) -> Boolean): Boolean = foldMap(AndMonoid, s, p)
}
