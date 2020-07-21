package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Setter
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotest.property.Arb
import io.kotest.property.forAll

object SetterLaws {

  fun <A, B> laws(setter: Setter<A, B>, aGen: Arb<A>, bGen: Arb<B>, funcGen: Arb<(B) -> B>, EQA: Eq<A>) = listOf(
    Law("Setter law: set is idempotent") { setter.setIdempotent(aGen, bGen, EQA) },
    Law("Setter law: modify identity") { setter.modifyIdentity(aGen, EQA) },
    Law("Setter law: compose modify") { setter.composeModify(aGen, EQA, funcGen) },
    Law("Setter law: consistent set modify") { setter.consistentSetModify(aGen, bGen, EQA) }
  )

  private suspend fun <A, B> Setter<A, B>.setIdempotent(aGen: Arb<A>, bGen: Arb<B>, EQA: Eq<A>) = forAll(aGen, bGen) { a, b ->
    set(set(a, b), b).equalUnderTheLaw(set(a, b), EQA)
  }

  private suspend fun <A, B> Setter<A, B>.modifyIdentity(aGen: Arb<A>, EQA: Eq<A>) = forAll(aGen) { a ->
    modify(a, ::identity).equalUnderTheLaw(a, EQA)
  }

  private suspend fun <A, B> Setter<A, B>.composeModify(aGen: Arb<A>, EQA: Eq<A>, funcGen: Arb<(B) -> B>) = forAll(aGen, funcGen, funcGen) { a, f, g ->
    modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), EQA)
  }

  private suspend fun <A, B> Setter<A, B>.consistentSetModify(aGen: Arb<A>, bGen: Arb<B>, EQA: Eq<A>) = forAll(aGen, bGen) { a, b ->
    modify(a) { b }.equalUnderTheLaw(set(a, b), EQA)
  }
}
