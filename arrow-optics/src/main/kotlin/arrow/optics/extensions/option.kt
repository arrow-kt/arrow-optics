package arrow.optics.extensions

import arrow.core.Option
import arrow.extension
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each

/**
 * [Traversal] for [Option] that has focus in each [arrow.core.Some].
 *
 * @receiver [Option.Companion] to make it statically available.
 * @return [Traversal] with source [Option] and focus in every [arrow.core.Some] of the source.
 */
fun <A> Option.Companion.traversal(): Traversal<Option<A>, A> =
  Traversal { s, f -> s.map(f) }

/**
 * [Each] instance definition for [Option].
 */
@extension
interface OptionEach<A> : Each<Option<A>, A> {
  override fun each(): Traversal<Option<A>, A> =
    Option.traversal()
}

inline fun <A> Option<A>.each(): Each<Option<A>, A> = Each { Option.traversal() }
