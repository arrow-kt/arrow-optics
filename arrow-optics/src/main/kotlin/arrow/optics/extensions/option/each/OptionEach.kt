package arrow.optics.extensions.option.each

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.optionEach
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
internal val each_singleton: Each<Option<Any?>, Any?> = optionEach()

@JvmName("each")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "optionEach()",
  "arrow.optics.optionEach"
  ),
  DeprecationLevel.WARNING
)
fun <A> each(): PTraversal<Option<A>, Option<A>, A, A> = arrow.core.Option
   .each<A>()
   .each() as arrow.optics.PTraversal<arrow.core.Option<A>, arrow.core.Option<A>, A, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Option.each<A>()",
    "arrow.optics.each"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.each(): Each<Option<A>, A> = each_singleton as
    arrow.optics.typeclasses.Each<Option<A>, A>
