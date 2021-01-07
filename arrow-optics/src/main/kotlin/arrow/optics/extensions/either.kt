package arrow.optics.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.fix
import arrow.core.extensions.either.traverse.traverse
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [Either] that has focus in each [Either.Right].
 *
 * @receiver [Either.Companion] to make it statically available.
 * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
 */
@Deprecated(
  "@extension kinded projected functions are deprecated. Use Either.traversal() instead",
  ReplaceWith("Either.traversal<L, R>()", "arrow.optics.traversal"),
  DeprecationLevel.WARNING)
fun <L, R> Either.Companion.traversal(): Traversal<Either<L, R>, R> = object : Traversal<Either<L, R>, R> {
  override fun <F> modifyF(FA: Applicative<F>, s: Either<L, R>, f: (R) -> Kind<F, R>): Kind<F, Either<L, R>> = with(Either.traverse<L>()) {
    FA.run { s.traverse(FA, f).map { it.fix() } }
  }
}

/**
 * [Each] instance for [Either] that has focus in each [Either.Right].
 */
@Deprecated(
  "@extension kinded projected functions are deprecated. Use Either.each() instead",
  ReplaceWith("Either.each<L, R>()", "arrow.core.Either", "arrow.optics.each"),
  DeprecationLevel.WARNING)
fun <L, R> eitherEach(): Each<Either<L, R>, R> = Each { Either.traversal() }
