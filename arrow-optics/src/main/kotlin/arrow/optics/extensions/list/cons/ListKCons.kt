package arrow.optics.extensions.list.cons

import arrow.core.ListK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.optics.POptional
import arrow.optics.PPrism
import arrow.optics.extensions.listKCons
import arrow.optics.typeclasses.Cons
import kotlin.collections.List

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "List::class.firstOption<A>()",
    "arrow.optics.firstOption"
  ),
  DeprecationLevel.WARNING
)
fun <A> firstOption(): POptional<ListK<A>, ListK<A>, A, A> = arrow.optics.extensions.list.cons.List
   .cons<A>()
   .firstOption() as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

@JvmName("tailOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "List::class.tailOption<A>()",
    "arrow.optics.tailOption"
  ),
  DeprecationLevel.WARNING
)
fun <A> tailOption(): POptional<ListK<A>, ListK<A>, ListK<A>, ListK<A>> =
    arrow.optics.extensions.list.cons.List
   .cons<A>()
   .tailOption() as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>,
    arrow.core.ListK<A>, arrow.core.ListK<A>>

@JvmName("cons")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "cons(tail)",
    "arrow.optics.cons"
  ),
  DeprecationLevel.WARNING
)
infix fun <A> A.cons(tail: List<A>): List<A> =
    arrow.optics.extensions.list.cons.List.cons<A>().run {
  this@cons.cons(arrow.core.ListK(tail)) as kotlin.collections.List<A>
}

@JvmName("uncons")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "uncons()",
    "arrow.optics.uncons"
  ),
  DeprecationLevel.WARNING
)
fun <A> List<A>.uncons(): Option<Tuple2<A, ListK<A>>> =
    arrow.optics.extensions.list.cons.List.cons<A>().run {
  arrow.core.ListK(this@uncons).uncons() as arrow.core.Option<arrow.core.Tuple2<A,
    arrow.core.ListK<A>>>
}

@JvmName("cons")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "List::class.cons<A>().cons()",
    "arrow.optics.cons"
  ),
  DeprecationLevel.WARNING
)
fun <A> cons(): PPrism<ListK<A>, ListK<A>, Tuple2<A, ListK<A>>, Tuple2<A, ListK<A>>> =
    arrow.optics.extensions.list.cons.List
   .cons<A>()
   .cons() as arrow.optics.PPrism<arrow.core.ListK<A>, arrow.core.ListK<A>, arrow.core.Tuple2<A,
    arrow.core.ListK<A>>, arrow.core.Tuple2<A, arrow.core.ListK<A>>>

/**
 * cached extension
 */
@PublishedApi()
internal val cons_singleton: Cons<ListK<Any?>, Any?> = listKCons()

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "@extension kinded projected functions are deprecated",
    ReplaceWith(
      "List::class.cons<A>()",
      "arrow.optics.cons"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> cons(): Cons<ListK<A>, A> = cons_singleton as arrow.optics.typeclasses.Cons<ListK<A>, A>
}