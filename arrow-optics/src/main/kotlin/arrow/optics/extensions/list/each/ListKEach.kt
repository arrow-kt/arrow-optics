package arrow.optics.extensions.list.each

import arrow.core.ListK
import arrow.optics.PTraversal
import arrow.optics.extensions.listKEach
import arrow.optics.typeclasses.Each

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
    "List::class.each<A>().each()",
    "arrow.optics.each"
  ),
  DeprecationLevel.WARNING
)
fun <A> each(): PTraversal<ListK<A>, ListK<A>, A, A> = arrow.optics.extensions.list.each.List
   .each<A>()
   .each() as arrow.optics.PTraversal<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

/**
 * cached extension
 */
@PublishedApi()
internal val each_singleton: Each<ListK<Any?>, Any?> = listKEach()

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "@extension kinded projected functions are deprecated",
    ReplaceWith(
      "List::class.each<A>()",
      "arrow.optics.each"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> each(): Each<ListK<A>, A> = each_singleton as arrow.optics.typeclasses.Each<ListK<A>, A>
}
