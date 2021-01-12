package arrow.optics.extensions.set.at

import arrow.core.SetK
import arrow.optics.PLens
import arrow.optics.extensions.SetKAt
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("at")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "at(i)",
  "arrow.core.at"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> PLens<T, T, SetK<A>, SetK<A>>.at(i: A): PLens<T, T, Boolean, Boolean> =
    arrow.optics.extensions.set.at.Set.at<A>().run {
  this@at.at<T>(i) as arrow.optics.PLens<T, T, kotlin.Boolean, kotlin.Boolean>
}

/**
 * cached extension
 */
@PublishedApi()
internal val at_singleton: SetKAt<Any?> = object : SetKAt<Any?> {}

object Set {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> at(): SetKAt<A> = at_singleton as arrow.optics.extensions.SetKAt<A>}
