package arrow.optics.extensions.listk.cons

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Option
import arrow.core.Tuple2
import arrow.optics.POptional
import arrow.optics.PPrism
import arrow.optics.extensions.listKCons
import arrow.optics.typeclasses.Cons
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val cons_singleton: Cons<ListK<Any?>, Any?> = listKCons()

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "ListK is being deprecated, use List top-level functions instead.",
  ReplaceWith(
    "List::class.firstOption<A>()",
    "kotlin.collections.List", "arrow.optics.firstOption"
  ),
  DeprecationLevel.WARNING
)
fun <A> firstOption(): POptional<ListK<A>, ListK<A>, A, A> = arrow.core.ListK
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
  "ListK is being deprecated, use List top-level functions instead.",
  ReplaceWith(
    "List::class.tailOption<A>()",
    "kotlin.collections.List", "arrow.optics.tailOption"
  ),
  DeprecationLevel.WARNING
)
fun <A> tailOption(): POptional<ListK<A>, ListK<A>, ListK<A>, ListK<A>> = arrow.core.ListK
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
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.cons",
  ReplaceWith(
    "cons(tail)",
    "arrow.optics.cons"
  ),
  DeprecationLevel.WARNING
)
infix fun <A> A.cons(tail: ListK<A>): ListK<A> = arrow.core.ListK.cons<A>().run {
  this@cons.cons(tail) as arrow.core.ListK<A>
}

@JvmName("uncons")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "ListK is being deprecated, use the uncons method define for List instead.",
  ReplaceWith(
    "uncons()",
    "arrow.optics.uncons"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListK<A>.uncons(): Option<Tuple2<A, ListK<A>>> = arrow.core.ListK.cons<A>().run {
  this@uncons.uncons() as arrow.core.Option<arrow.core.Tuple2<A, arrow.core.ListK<A>>>
}

@JvmName("cons")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.cons",
  ReplaceWith(
    "List::class.cons<A>().cons()",
    "kotlin.collections.List", "arrow.optics.cons"
  ),
  DeprecationLevel.WARNING
)
fun <A> cons(): PPrism<ListK<A>, ListK<A>, Tuple2<A, ListK<A>>, Tuple2<A, ListK<A>>> =
    arrow.core.ListK
   .cons<A>()
   .cons() as arrow.optics.PPrism<arrow.core.ListK<A>, arrow.core.ListK<A>, arrow.core.Tuple2<A,
    arrow.core.ListK<A>>, arrow.core.Tuple2<A, arrow.core.ListK<A>>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "ListK is being deprecated, use List top-level functions instead.",
  ReplaceWith(
    "List::class.cons<A>()",
    "kotlin.collections.List", "arrow.optics.cons"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.cons(): Cons<ListK<A>, A> = cons_singleton as
    arrow.optics.typeclasses.Cons<ListK<A>, A>
