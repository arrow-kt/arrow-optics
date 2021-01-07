package arrow.optics.extensions.either.each

import arrow.core.Either
import arrow.core.Either.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.eitherEach
import arrow.optics.typeclasses.Each
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val each_singleton: Each<Either<Any?, Any?>, Any?> = eitherEach()

@JvmName("each")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Use Either.each().each() instead",
  ReplaceWith("Either.each<L, R>().each()", "arrow.core.Either", "arrow.optics.each"),
  DeprecationLevel.WARNING
)
fun <L, R> each(): PTraversal<Either<L, R>, Either<L, R>, R, R> = arrow.core.Either
   .each<L, R>()
   .each() as arrow.optics.PTraversal<arrow.core.Either<L, R>, arrow.core.Either<L, R>, R, R>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Use Either.each() instead",
  ReplaceWith("Either.each<L, R>()", "arrow.optics.each"),
  DeprecationLevel.WARNING)
inline fun <L, R> Companion.each(): Each<Either<L, R>, R> = each_singleton as
    arrow.optics.typeclasses.Each<Either<L, R>, R>
