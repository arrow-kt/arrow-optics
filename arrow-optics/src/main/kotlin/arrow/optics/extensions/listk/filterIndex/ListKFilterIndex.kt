package arrow.optics.extensions.listk.filterIndex

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.listKFilterIndex
import arrow.optics.typeclasses.FilterIndex
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val filterIndex_singleton: FilterIndex<ListK<Any?>, Int, Any?> = listKFilterIndex()

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
fun <A> filter(p: Function1<Int, Boolean>): PTraversal<ListK<A>, ListK<A>, A, A> = arrow.core.ListK
   .filterIndex<A>()
   .filter(p) as arrow.optics.PTraversal<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

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
inline fun <A> Companion.filterIndex(): FilterIndex<ListK<A>, Int, A> =
  filterIndex_singleton as arrow.optics.typeclasses.FilterIndex<ListK<A>, Int, A>
