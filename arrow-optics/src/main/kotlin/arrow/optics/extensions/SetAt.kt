package arrow.optics.extensions

import arrow.core.SetExtensions
import arrow.optics.Lens
import arrow.optics.PLens
import arrow.optics.typeclasses.At
import arrow.typeclasses.Eq

/**
 * [At] instance definition for [Set].
 */
interface SetAt<A> : At<Set<A>, A, Boolean> {
  override fun at(i: A): Lens<Set<A>, Boolean> = PLens(
    get = { it.contains(i) },
    set = { s, b -> (if (b) s + i else s - i) }
  )

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <A> invoke() = object : SetAt<A> {}
  }
}

fun <A> SetExtensions.at(): SetAt<A> = SetAt()

// TODO: Move to Arrow Core
interface SetEq<A> : Eq<Set<A>> {
  fun EQA(): Eq<A>

  override fun Set<A>.eqv(b: Set<A>): Boolean =
    if (this.size == b.size) {
      this.map { a ->
        b.find { x ->
          EQA().run { a.eqv(x) }
        } != null
      }.all { it }
    } else {
      false
    }

  companion object {
    operator fun <A> invoke(eqa: Eq<A>): SetEq<A> = object : SetEq<A> {
      override fun EQA(): Eq<A> = eqa
    }
  }
}

fun <A> SetExtensions.eq(eqa: Eq<A>): SetEq<A> = SetEq(eqa)
