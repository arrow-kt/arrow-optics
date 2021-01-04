package arrow.optics.extensions

import arrow.Kind
import arrow.core.Option
import arrow.core.extensions.option.traverse.traverse
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [Option] that has focus in each [arrow.core.Some].
 *
 * @receiver [Option.Companion] to make it statically available.
 * @return [Traversal] with source [Option] and focus in every [arrow.core.Some] of the source.
 */
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Option.traversal<A>()",
    "arrow.optics.traversal"
  ),
  DeprecationLevel.WARNING
)
fun <A> Option.Companion.traversal(): Traversal<Option<A>, A> = object : Traversal<Option<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: Option<A>, f: (A) -> Kind<F, A>): Kind<F, Option<A>> = with(Option.traverse()) {
    s.traverse(FA, f)
  }
}

/**
 * [Each] instance definition for [Option].
 */
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Option.each<A>()",
    "arrow.optics.each"
  ),
  DeprecationLevel.WARNING
)
fun <A> optionEach(): Each<Option<A>, A> = Each { Option.traversal() }
