package arrow.optics.extensions.list.filterIndex

import arrow.core.ListK
import arrow.optics.PTraversal
import arrow.optics.extensions.listKFilterIndex
import arrow.optics.typeclasses.FilterIndex

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
    "List::class.filter<A>(p)",
    "arrow.optics.filter"
  ),
  DeprecationLevel.WARNING
)
fun <A> filter(p: Function1<Int, Boolean>): PTraversal<ListK<A>, ListK<A>, A, A> =
    arrow.optics.extensions.list.filterIndex.List
   .filterIndex<A>()
   .filter(p) as arrow.optics.PTraversal<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

/**
 * cached extension
 */
@PublishedApi()
internal val filterIndex_singleton: FilterIndex<ListK<Any?>, Int, Any?> = listKFilterIndex()

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "@extension kinded projected functions are deprecated",
    ReplaceWith(
      "List::class.filterIndex<A>()",
      "arrow.optics.filterIndex"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> filterIndex(): FilterIndex<ListK<A>, Int, A> = filterIndex_singleton as
      arrow.optics.typeclasses.FilterIndex<ListK<A>, Int, A>
}
