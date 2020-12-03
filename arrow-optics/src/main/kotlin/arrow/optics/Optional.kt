package arrow.optics

import arrow.Kind
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.toT
import arrow.typeclasses.Applicative

/**
 * [Optional] is a type alias for [POptional] which fixes the type arguments
 * and restricts the [POptional] to monomorphic updates.
 */
typealias Optional<S, A> = POptional<S, S, A, A>

@Suppress("FunctionName")
fun <S, A> Optional(getOption: (source: S) -> Option<A>, set: (source: S, focus: A) -> S): Optional<S, A> =
  POptional({ s -> getOption(s).toEither { s } }, set)

/**
 * [Optional] is an optic that allows to focus into a structure and querying or [copy]'ing an optional focus.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.optics.Optional
 *
 * data class User(val username: String, val email: Option<String>) {
 *     companion object {
 *       // can be out generated by @optics
 *       val email: Optional<User, String> = Optional(User::email) { user, email ->
 *         user.copy(email = Some(email))
 *       }
 *   }
 * }
 *
 * fun main(args: Array<String>) {
 *   val original = User("arrow-user", None)
 *   val set = User.email.set(original, "arRoW-UsEr@arrow-Kt.IO")
 *   val modified = User.email.modify(set, String::toLowerCase)
 *   println("original: $original, set: $set, modified: $modified")
 * }
 * ```
 *
 * A (polymorphic) [POptional] is useful when setting or modifying a value for a type with a optional polymorphic focus
 * i.e. POptional<Either<Int, Double>, Either<String, Double>, Int, String>
 *
 * A [POptional] can be seen as a weaker [Lens] and [Prism] and combines their weakest functions:
 * - `set: (S, B) -> T` meaning we can focus into an `S` and set a value `B` for a target `A` and obtain a modified source `T`
 * - `getOrModify: (S) -> Either<T, A>` meaning it returns the focus of a [POptional] OR the original value
 *
 * @param S the source of a [POptional]
 * @param T the modified source of a [POptional]
 * @param A the focus of a [POptional]
 * @param B the modified focus of a [POptional]
 */
interface POptional<S, T, A, B> {

  /**
   * Get the modified source of a [POptional]
   */
  fun set(source: S, focus: B): T

  /**
   * Get the focus of a [POptional] or return the original value while allowing the type to change if it does not match
   */
  fun getOrModify(source: S): Either<T, A>

  companion object {

    fun <S> id() = PIso.id<S>().asOptional()

    /**
     * [POptional] that takes either [S] or [S] and strips the choice of [S].
     */
    fun <S> codiagonal(): Optional<Either<S, S>, S> = POptional(
      { sources -> sources.fold({ Either.Right(it) }, { Either.Right(it) }) },
      { sources, focus -> sources.bimap({ focus }, { focus }) }
    )

    /**
     * Invoke operator overload to create a [POptional] of type `S` with focus `A`.
     * Can also be used to construct [Optional]
     */
    operator fun <S, T, A, B> invoke(getOrModify: (source: S) -> Either<T, A>, set: (source: S, focus: B) -> T): POptional<S, T, A, B> = object : POptional<S, T, A, B> {
      override fun getOrModify(source: S): Either<T, A> = getOrModify(source)

      override fun set(source: S, focus: B): T = set(source, focus)
    }

    /**
     * [POptional] that never sees its focus
     */
    fun <A, B> void(): Optional<A, B> = POptional(
      { Either.Left(it) },
      { source, _ -> source }
    )
  }

  /**
   * Modify the focus of a [POptional] with an Applicative function [f]
   */
  fun <F> modifyF(FA: Applicative<F>, source: S, f: (focus: A) -> Kind<F, B>): Kind<F, T> = FA.run {
    getOrModify(source).fold(
      ::just
    ) { focus -> f(focus).map { set(source, it) } }
  }

  /**
   * Lift a function [f]: `(A) -> Kind<F, B> to the context of `S`: `(S) -> Kind<F, T>`
   */
  fun <F> liftF(FA: Applicative<F>, f: (focus: A) -> Kind<F, B>): (source: S) -> Kind<F, T> = { source ->
    modifyF(FA, source, f)
  }

  /**
   * Get the focus of a [POptional] or [Option.None] if the is not there
   */
  fun getOption(source: S): Option<A> = getOrModify(source).toOption()

  /**
   * Set the focus of a [POptional] with a value.
   * @return [Option.None] if the [POptional] is not matching
   */
  fun setOption(source: S, b: B): Option<T> = modifyOption(source) { b }

  /**
   * Check if there is no focus
   */
  fun isEmpty(source: S): Boolean = !nonEmpty(source)

  /**
   * Check if there is a focus
   */
  fun nonEmpty(source: S): Boolean = getOption(source).fold({ false }, { true })

  /**
   * Join two [POptional] with the same focus [B]
   */
  infix fun <S1, T1> choice(other: POptional<S1, T1, A, B>): POptional<Either<S, S1>, Either<T, T1>, A, B> =
    POptional(
      { sources ->
        sources.fold(
          { leftSource ->
            getOrModify(leftSource).bimap({ Either.Left(it) }, ::identity)
          },
          { rightSource ->
            other.getOrModify(rightSource).bimap({ Either.Right(it) }, ::identity)
          }
        )
      },
      { sources, focus ->
        sources.bimap(
          { leftSource -> this.set(leftSource, focus) },
          { rightSource -> other.set(rightSource, focus) }
        )
      }
    )

  /**
   * Create a product of the [POptional] and a type [C]
   */
  fun <C> first(): POptional<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> =
    POptional(
      { (source, c) -> getOrModify(source).bimap({ it toT c }, { it toT c }) },
      { (source, c2), (update, c) -> setOption(source, update).fold({ set(source, update) toT c2 }, { it toT c }) }
    )

  /**
   * Create a product of a type [C] and the [POptional]
   */
  fun <C> second(): POptional<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> =
    POptional(
      { (c, s) -> getOrModify(s).bimap({ c toT it }, { c toT it }) },
      { (c2, s), (c, b) -> setOption(s, b).fold({ c2 toT set(s, b) }, { c toT it }) }
    )

  /**
   * Compose a [POptional] with a [POptional]
   */
  infix fun <C, D> compose(other: POptional<A, B, C, D>): POptional<S, T, C, D> = POptional(
    { source -> getOrModify(source).flatMap { a -> other.getOrModify(a).bimap({ b -> set(source, b) }, ::identity) } },
    { source, d -> modify(source) { a -> other.set(a, d) } }
  )

  /**
   * Compose a [POptional] with a [PPrism]
   */
  infix fun <C, D> compose(other: PPrism<A, B, C, D>): POptional<S, T, C, D> = compose(other.asOptional())

  /**
   * Compose a [POptional] with a [PLens]
   */
  infix fun <C, D> compose(other: PLens<A, B, C, D>): POptional<S, T, C, D> = compose(other.asOptional())

  /**
   * Compose a [POptional] with a [PIso]
   */
  infix fun <C, D> compose(other: PIso<A, B, C, D>): POptional<S, T, C, D> = compose(other.asOptional())

  /**
   * Compose a [POptional] with a [PIso]
   */
  infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

  /**
   * Compose a [POptional] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = asFold() compose other

  /**
   * Compose a [POptional] with a [Fold]
   */
  infix fun <C> compose(other: Getter<A, C>): Fold<S, C> = asFold() compose other

  /**
   * Compose a [POptional] with a [PTraversal]
   */
  infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = asTraversal() compose other

  /**
   * Plus operator overload to compose optionals
   */
  operator fun <C, D> plus(other: POptional<A, B, C, D>): POptional<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PPrism<A, B, C, D>): POptional<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PLens<A, B, C, D>): POptional<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PIso<A, B, C, D>): POptional<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(other)

  operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Getter<A, C>): Fold<S, C> = compose(other)

  operator fun <C, D> plus(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  /**
   * View a [POptional] as a [PSetter]
   */
  fun asSetter(): PSetter<S, T, A, B> = object : PSetter<S, T, A, B> {
    override fun modify(s: S, f: (A) -> B): T = this@POptional.modify(s, f)

    override fun set(s: S, b: B): T = this@POptional.set(s, b)
  }

  /**
   * View a [POptional] as a [Fold]
   */
  fun asFold() = object : Fold<S, A> {
    override fun <R> foldMap(s: S, empty: R, combine: (R, R) -> R, map: (A) -> R): R =
      getOption(s).map(map).getOrElse { empty }
  }

  /**
   * View a [POptional] as a [PTraversal]
   */
  fun asTraversal(): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
    override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
      this@POptional.modifyF(FA, s, f)
  }

  /**
   * Modify the focus of a [POptional] with a function [f]
   */
  fun modify(source: S, f: (focus: A) -> B): T = getOrModify(source).fold(::identity) { a -> set(source, f(a)) }

  /**
   * Lift a function [f]: `(A) -> B to the context of `S`: `(S) -> T`
   */
  fun lift(f: (focus: A) -> B): (S) -> T = { s -> modify(s, f) }

  /**
   * Modify the focus of a [POptional] with a function [f]
   * @return [Option.None] if the [POptional] is not matching
   */
  fun modifyOption(source: S, f: (focus: A) -> B): Option<T> = getOption(source).map { set(source, f(it)) }

  /**
   * Find the focus that satisfies the predicate [predicate]
   */
  fun find(source: S, predicate: (focus: A) -> Boolean): Option<A> =
    getOption(source).flatMap { b -> if (predicate(b)) Some(b) else None }

  /**
   * Check if there is a focus and it satisfies the predicate [predicate]
   */
  fun exists(source: S, predicate: (focus: A) -> Boolean): Boolean = getOption(source).fold({ false }, predicate)

  /**
   * Check if there is no focus or the target satisfies the predicate [predicate]
   */
  fun all(source: S, predicate: (focus: A) -> Boolean): Boolean = getOption(source).fold({ true }, predicate)
}
