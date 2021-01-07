package arrow.optics

import arrow.Kind
import arrow.core.Either
import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated
import arrow.core.extensions.either.traverse.traverse
import arrow.core.fix
import arrow.optics.extensions.either.each.each
import arrow.optics.extensions.traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

/**
 * [PIso] that defines the equality between [Either] and [Validated]
 */
fun <A1, A2, B1, B2> Either.Companion.toPValidated(): PIso<Either<A1, B1>, Either<A2, B2>, Validated<A1, B1>, Validated<A2, B2>> = PIso(
  get = { it.fold(::Invalid, ::Valid) },
  reverseGet = Validated<A2, B2>::toEither
)

/**
 * [Iso] that defines the equality between [Either] and [Validated]
 */
fun <A, B> Either.Companion.toValidated(): Iso<Either<A, B>, Validated<A, B>> = toPValidated()

/**
 * [Each] instance for [Either] that has focus in each [Either.Right].
 */
@Suppress(
  "NOTHING_TO_INLINE"
)
inline fun <L, R> Either.Companion.each(): Each<Either<L, R>, R> = Each { Either.traversal() }

/**
 * [Traversal] for [Either] that has focus in each [Either.Right].
 *
 * @receiver [Either.Companion] to make it statically available.
 * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
 */
fun <L, R> Either.Companion.traversal(): Traversal<Either<L, R>, R> =
  object : Traversal<Either<L, R>, R> {
    override fun <F> modifyF(FA: Applicative<F>, s: Either<L, R>, f: (R) -> Kind<F, R>): Kind<F, Either<L, R>> =
      with(Either.traverse<L>()) {
        FA.run { s.traverse(FA, f).map { it.fix() } }
      }
  }

fun <L, R> eitherEach(): PTraversal<Either<L, R>, Either<L, R>, R, R> = Either
  .each<L, R>()
  .each()
