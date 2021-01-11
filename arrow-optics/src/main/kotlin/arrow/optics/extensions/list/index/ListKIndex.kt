package arrow.optics.extensions.list.index

import arrow.core.ListK
import arrow.optics.POptional
import arrow.optics.extensions.listKIndex
import arrow.optics.typeclasses.Index

@JvmName("index")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.index",
  ReplaceWith(
    "List::class.index<A>(i)",
    "kotlin.collections.List", "arrow.optics.index"
  ),
  DeprecationLevel.WARNING
)
fun <A> index(i: Int): POptional<ListK<A>, ListK<A>, A, A> = arrow.optics.extensions.list.index.List
   .index<A>()
   .index(i) as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

/**
 * cached extension
 */
@PublishedApi()
internal val index_singleton: Index<ListK<Any?>, Int, Any?> = listKIndex()

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.index",
    ReplaceWith(
      "List::class.index<A>()",
      "kotlin.collections.List", "arrow.optics.index"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> index(): Index<ListK<A>, Int, A> = index_singleton as arrow.optics.typeclasses.Index<ListK<A>, Int, A>
}
