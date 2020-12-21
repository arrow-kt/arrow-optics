package arrow.optics.extensions

import arrow.core.SetExtensions
import arrow.optics.Lens
import arrow.optics.PLens
import arrow.optics.typeclasses.At
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.reflect.KClass

/**
 * [At] instance definition for [Set].
 */
fun <A> setAt(): At<Set<A>, A, Boolean> = At { i ->
  PLens(
    get = { it.contains(i) },
    set = { s, b -> (if (b) s + i else s - i) }
  )
}

@Deprecated("Instance should be obtained through Set class", ReplaceWith("Set::class.at<A>()"))
fun <A> SetExtensions.at(): At<Set<A>, A, Boolean> = Set::class.at()

fun <A> KClass<Set<*>>.at(): At<Set<A>, A, Boolean> = setAt()

fun <A> KClass<Set<*>>.at(i: A): Lens<Set<A>, Boolean> = Set::class.at<A>().at(i)

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

@Deprecated("Instance should be obtained through Set class", ReplaceWith("Set::class.eq<A>(eqa)"))
fun <A> SetExtensions.eq(eqa: Eq<A>): SetEq<A> = SetEq(eqa)

fun <A> KClass<Set<*>>.eq(eqa: Eq<A>): SetEq<A> = SetEq(eqa)

// TODO: Move to Arrow Core
interface SetSemigroup<A> : Semigroup<Set<A>> {
  override fun Set<A>.combine(b: Set<A>): Set<A> = this + b

  companion object {
    operator fun <A> invoke(): SetSemigroup<A> = object : SetSemigroup<A> {}
  }
}

fun <A> KClass<Set<*>>.semigroup(): SetSemigroup<A> = SetSemigroup()

// TODO: Move to Arrow Core
interface SetMonoid<A> : Monoid<Set<A>>, SetSemigroup<A> {

  override fun empty(): Set<A> = emptySet()

  companion object {
    operator fun <A> invoke(): SetMonoid<A> = object : SetMonoid<A> {}
  }
}

fun <A> KClass<Set<*>>.monoid(): SetMonoid<A> = SetMonoid()
