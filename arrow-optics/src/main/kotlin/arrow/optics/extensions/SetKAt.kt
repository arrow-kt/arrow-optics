package arrow.optics.extensions

import arrow.core.SetK
import arrow.core.k
import arrow.optics.Lens
import arrow.optics.PLens
import arrow.optics.typeclasses.At

/**
 * [At] instance definition for [SetK].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "At<SetK<A>, A, Boolean>",
    "arrow.optics.typeclasses.At", "arrow.core.SetK"),
  DeprecationLevel.WARNING)
interface SetKAt<A> : At<SetK<A>, A, Boolean> {
  override fun at(i: A): Lens<SetK<A>, Boolean> = PLens(
    get = { it.contains(i) },
    set = { s, b -> (if (b) s + i else s - i).k() }
  )
}
