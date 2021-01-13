package arrow.optics.extensions.sequence.index

import arrow.core.SequenceK
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.extensions.SequenceKIndex
import kotlin.Any
import kotlin.Deprecated
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("index")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "index(i)",
  "arrow.optics.extensions.sequence.index.Sequence.index"
  ),
  DeprecationLevel.WARNING
)
fun <A> index(i: Int): POptional<SequenceK<A>, SequenceK<A>, A, A> =
    arrow.optics.extensions.sequence.index.Sequence
   .index<A>()
   .index(i) as arrow.optics.POptional<arrow.core.SequenceK<A>, arrow.core.SequenceK<A>, A, A>

@JvmName("get")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "get(i)",
  "arrow.core.get"
  ),
  DeprecationLevel.WARNING
)
operator fun <A, T> PLens<T, T, SequenceK<A>, SequenceK<A>>.get(i: Int): POptional<T, T, A, A> =
    arrow.optics.extensions.sequence.index.Sequence.index<A>().run {
  this@get.get<T>(i) as arrow.optics.POptional<T, T, A, A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val index_singleton: SequenceKIndex<Any?> = object : SequenceKIndex<Any?> {}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> index(): SequenceKIndex<A> = index_singleton as
      arrow.optics.extensions.SequenceKIndex<A>}
