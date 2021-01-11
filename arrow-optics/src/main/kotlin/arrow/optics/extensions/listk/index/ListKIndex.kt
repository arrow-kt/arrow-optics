package arrow.optics.extensions.listk.index

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.extensions.listKIndex
import arrow.optics.typeclasses.Index
import kotlin.Any
import kotlin.Deprecated
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val index_singleton: Index<ListK<Any?>, Int, Any?> = listKIndex()

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
fun <A> index(i: Int): POptional<ListK<A>, ListK<A>, A, A> = arrow.core.ListK
   .index<A>()
   .index(i) as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

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
operator fun <A, T> PLens<T, T, ListK<A>, ListK<A>>.get(i: Int): POptional<T, T, A, A> =
    arrow.core.ListK.index<A>().run {
  this@get.get<T>(i) as arrow.optics.POptional<T, T, A, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "ListK is being deprecated, use List top-level functions instead.",
  ReplaceWith(
    "List::class.index<A>()",
    "kotlin.collections.List", "arrow.optics.index"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.index(): Index<ListK<A>, Int, A> = index_singleton as
    arrow.optics.typeclasses.Index<ListK<A>, Int, A>
