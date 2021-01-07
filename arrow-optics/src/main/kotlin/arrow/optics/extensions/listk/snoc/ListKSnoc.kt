package arrow.optics.extensions.listk.snoc

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Option
import arrow.core.Tuple2
import arrow.optics.POptional
import arrow.optics.extensions.listKSnoc
import arrow.optics.typeclasses.Snoc
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val snoc_singleton: Snoc<ListK<Any?>, Any?> = listKSnoc()

@JvmName("initOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "List::class.initOption<A>()",
    "arrow.optics.initOption"
  ),
  DeprecationLevel.WARNING
)
fun <A> initOption(): POptional<ListK<A>, ListK<A>, ListK<A>, ListK<A>> = arrow.core.ListK
   .snoc<A>()
   .initOption() as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>,
    arrow.core.ListK<A>, arrow.core.ListK<A>>

@JvmName("lastOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "List::class.lastOption<A>()",
    "arrow.optics.lastOption"
  ),
  DeprecationLevel.WARNING
)
fun <A> lastOption(): POptional<ListK<A>, ListK<A>, A, A> = arrow.core.ListK
   .snoc<A>()
   .lastOption() as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

@JvmName("snoc")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "snoc(last)",
    "arrow.optics.snoc"
  ),
  DeprecationLevel.WARNING
)
infix fun <A> ListK<A>.snoc(last: A): ListK<A> = arrow.core.ListK.snoc<A>().run {
  this@snoc.snoc(last) as arrow.core.ListK<A>
}

@JvmName("unsnoc")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "unsnoc()",
    "arrow.optics.unsnoc"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListK<A>.unsnoc(): Option<Tuple2<ListK<A>, A>> = arrow.core.ListK.snoc<A>().run {
  this@unsnoc.unsnoc() as arrow.core.Option<arrow.core.Tuple2<arrow.core.ListK<A>, A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "List::class.snoc<A>()",
    "arrow.optics.snoc"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.snoc(): Snoc<ListK<A>, A> = snoc_singleton as
    arrow.optics.typeclasses.Snoc<ListK<A>, A>