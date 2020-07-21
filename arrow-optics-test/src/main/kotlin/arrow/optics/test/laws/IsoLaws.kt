package arrow.optics.test.laws

import arrow.core.Const
import arrow.core.Id
import arrow.core.compose
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.id.functor.functor
import arrow.core.identity
import arrow.core.value
import arrow.optics.Iso
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.forAll

object IsoLaws {

  fun <A, B> laws(iso: Iso<A, B>, aGen: Arb<A>, bGen: Arb<B>, funcGen: Arb<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, bMonoid: Monoid<B>): List<Law> =
    listOf(
      Law("Iso Law: round trip one way") { iso.roundTripOneWay(aGen, EQA) },
      Law("Iso Law: round trip other way") { iso.roundTripOtherWay(bGen, EQB) },
      Law("Iso Law: modify identity is identity") { iso.modifyIdentity(aGen, EQA) },
      Law("Iso Law: compose modify") { iso.composeModify(aGen, funcGen, EQA) },
      Law("Iso Law: consitent set with modify") { iso.consistentSetModify(aGen, bGen, EQA) },
      Law("Iso Law: consistent modify with modify identity") { iso.consistentModifyModifyId(aGen, funcGen, EQA) },
      Law("Iso Law: consitent get with modify identity") { iso.consitentGetModifyId(aGen, EQB, bMonoid) }
    )

  private suspend fun <A, B> Iso<A, B>.roundTripOneWay(aGen: Arb<A>, EQA: Eq<A>) =
    forAll(aGen) { a ->
      reverseGet(get(a)).equalUnderTheLaw(a, EQA)
    }

  private suspend fun <A, B> Iso<A, B>.roundTripOtherWay(bGen: Arb<B>, EQB: Eq<B>) =
    forAll(bGen) { b ->
      get(reverseGet(b)).equalUnderTheLaw(b, EQB)
    }

  private suspend fun <A, B> Iso<A, B>.modifyIdentity(aGen: Arb<A>, EQA: Eq<A>) =
    forAll(aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, EQA)
    }

  private suspend fun <A, B> Iso<A, B>.composeModify(aGen: Arb<A>, funcGen: Arb<(B) -> B>, EQA: Eq<A>) =
    forAll(aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), EQA)
    }

  private suspend fun <A, B> Iso<A, B>.consistentSetModify(aGen: Arb<A>, bGen: Arb<B>, EQA: Eq<A>) =
    forAll(aGen, bGen) { a, b ->
      set(b).equalUnderTheLaw(modify(a) { b }, EQA)
    }

  private suspend fun <A, B> Iso<A, B>.consistentModifyModifyId(aGen: Arb<A>, funcGen: Arb<(B) -> B>, EQA: Eq<A>) =
    forAll(aGen, funcGen) { a, f ->
      modify(a, f).equalUnderTheLaw(modifyF(Id.functor(), a) { Id.just(f(it)) }.value(), EQA)
    }

  private suspend fun <A, B> Iso<A, B>.consitentGetModifyId(aGen: Arb<A>, EQB: Eq<B>, bMonoid: Monoid<B>) =
    forAll(aGen) { a ->
      get(a).equalUnderTheLaw(modifyF(Const.applicative(bMonoid), a, ::Const).value(), EQB)
    }
}
