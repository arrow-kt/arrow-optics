package arrow.optics.extensions

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.optics.Prism
import arrow.optics.Traversal
import arrow.optics.toList
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [String] that focuses in each [Char] of the source [String].
 *
 * @receiver [String.Companion] to make it statically available.
 * @return [Traversal] with source [String] and foci every [Char] in the source.
 */
fun String.Companion.traversal(): Traversal<String, Char> =
  Traversal { s, f ->
    s.map(f).joinToString("")
  }

/**
 * [String]'s [Each] instance
 * @see StringEachInstance
 * @receiver [String.Companion] to make the instance statically available.
 * @return [Each] instance
 */
fun String.Companion.each(): Each<String, Char> = stringEach()

/**
 * [Each] instance for [String].
 */
inline fun stringEach(): Each<String, Char> = Each { String.traversal() }

/**
 * [String]'s [FilterIndex] instance
 *
 * @see StringFilterIndexInstance
 * @receiver [String.Companion] to make the instance statically available.
 * @return [FilterIndex] instance
 */
fun String.Companion.filterIndex(): FilterIndex<String, Int, Char> = stringFilterIndex()

/**
 * [FilterIndex] instance for [String].
 * It allows filtering of every [Char] in a [String] by its index's position.
 */
inline fun stringFilterIndex(): FilterIndex<String, Int, Char> = FilterIndex { p ->
  String.toList() compose List::class.filterIndex<Char>().filter(p)
}

/**
 * [String]'s [Index] instance
 * It allows access to every [Char] in a [String] by its index's position.
 *
 * @see StringIndexInstance
 * @receiver [String.Companion] to make the instance statically available.
 * @return [Index] instance
 */
fun String.Companion.index(): Index<String, Int, Char> = stringIndex()

/**
 * [Index] instance for [String].
 * It allows access to every [Char] in a [String] by its index's position.
 */
inline fun stringIndex(): Index<String, Int, Char> = Index { i ->
  String.toList() compose List::class.index<Char>().index(i)
}

/**
 * [String]'s [Cons] instance
 */
fun String.Companion.cons(): Cons<String, Char> = stringCons()

inline fun stringCons(): Cons<String, Char> = Cons {
  Prism(
    getOrModify = { if (it.isNotEmpty()) Tuple2(it.first(), it.drop(1)).right() else it.left() },
    reverseGet = { (h, t) -> h + t }
  )
}

/**
 * [String]'s [Snoc] instance
 */
fun String.Companion.snoc(): Snoc<String, Char> = stringSnoc()

inline fun stringSnoc(): Snoc<String, Char> = Snoc {
  Prism(
    getOrModify = { if (it.isNotEmpty()) Tuple2(it.dropLast(1), it.last()).right() else it.left() },
    reverseGet = { (i, l) -> i + l }
  )
}
