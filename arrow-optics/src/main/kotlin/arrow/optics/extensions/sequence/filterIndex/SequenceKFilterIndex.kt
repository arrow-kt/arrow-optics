package arrow.optics.extensions.sequence.filterIndex

import arrow.core.SequenceK
import arrow.optics.PTraversal
import arrow.optics.extensions.SequenceKFilterIndex
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "filter(p)",
  "arrow.optics.extensions.sequence.filterIndex.Sequence.filter"
  ),
  DeprecationLevel.WARNING
)
fun <A> filter(p: Function1<Int, Boolean>): PTraversal<SequenceK<A>, SequenceK<A>, A, A> =
    arrow.optics.extensions.sequence.filterIndex.Sequence
   .filterIndex<A>()
   .filter(p) as arrow.optics.PTraversal<arrow.core.SequenceK<A>, arrow.core.SequenceK<A>, A, A>

/**
 * cached extension
 */
@PublishedApi()
internal val filterIndex_singleton: SequenceKFilterIndex<Any?> = object : SequenceKFilterIndex<Any?>
    {}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> filterIndex(): SequenceKFilterIndex<A> = filterIndex_singleton as
      arrow.optics.extensions.SequenceKFilterIndex<A>}
