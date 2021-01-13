package arrow.optics.extensions.sequencek.each

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.SequenceKEach
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val each_singleton: SequenceKEach<Any?> = object : SequenceKEach<Any?> {}

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
  "each()",
  "arrow.core.SequenceK.each"
  ),
  DeprecationLevel.WARNING
)
fun <A> each(): PTraversal<SequenceK<A>, SequenceK<A>, A, A> = arrow.core.SequenceK
   .each<A>()
   .each() as arrow.optics.PTraversal<arrow.core.SequenceK<A>, arrow.core.SequenceK<A>, A, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.each(): SequenceKEach<A> = each_singleton as
    arrow.optics.extensions.SequenceKEach<A>
