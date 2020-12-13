package arrow.optics

import arrow.core.Const
import arrow.core.None
import arrow.core.Option
import arrow.typeclasses.Monoid

@PublishedApi
@Deprecated("AndMonoid has been refactored", ReplaceWith("AndMonoid", "arrow.core.extensions.AndMonoid"))
internal object AndMonoid : Monoid<Boolean> {
  override fun Boolean.combine(b: Boolean): Boolean = this && b
  override fun empty(): Boolean = true
}


@PublishedApi
internal fun <A> firstOptionMonoid(): Monoid<Option<A>> = object : Monoid<Option<A>> {
  override fun empty(): Option<A> = None

  override fun Option<A>.combine(b: Option<A>): Option<A> =
    if (isDefined()) this else b
}

@PublishedApi
internal fun <A> lastOptionMonoid(): Monoid<Option<A>> = object : Monoid<Option<A>> {
  override fun empty(): Option<A> = None

  override fun Option<A>.combine(b: Option<A>): Option<A> =
    if (b.isDefined()) b else this
}
