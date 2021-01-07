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
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "List::class.index<A>(i)",
    "arrow.optics.index"
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

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "@extension kinded projected functions are deprecated",
    ReplaceWith(
      "List::class.index<A>()",
      "arrow.optics.index"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> index(): Index<ListK<A>, Int, A> = index_singleton as arrow.optics.typeclasses.Index<ListK<A>, Int, A>
}
