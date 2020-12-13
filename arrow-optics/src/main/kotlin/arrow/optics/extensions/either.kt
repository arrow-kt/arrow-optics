package arrow.optics.extensions

import arrow.core.Either
import arrow.extension
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each

/**
 * [Traversal] for [Either] that has focus in each [Either.Right].
 *
 * @receiver [Either.Companion] to make it statically available.
 * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
 */
fun <L, R> Either.Companion.traversal(): Traversal<Either<L, R>, R> =
  Traversal { s, f -> s.map(f) }

/**
 * [Each] instance for [Either] that has focus in each [Either.Right].
 */
@extension
interface EitherEach<L, R> : Each<Either<L, R>, R> {
  override fun each(): Traversal<Either<L, R>, R> =
    Either.traversal()
}

inline fun <L, R> Either<L, R>.each(): Each<Either<L, R>, R> = Each { Either.traversal() }
