package arrow.optics

import arrow.core.ListExtensions
import arrow.core.ListK

private val stringToList: Iso<String, List<Char>> = Iso(
  get = CharSequence::toList,
  reverseGet = { it.joinToString(separator = "") }
)

/**
 * [Iso] that defines equality between String and [List] of [Char]
 */
fun String.Companion.toList(): Iso<String, List<Char>> =
  stringToList

