package arrow.optics.test.laws

import arrow.core.Option
import arrow.core.compose
import arrow.core.identity
import arrow.core.toOption
import arrow.core.ListK
import arrow.optics.Traversal
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotest.property.Arb
import io.kotest.property.forAll

object TraversalLaws {

  fun <A, B : Any> laws(traversal: Traversal<A, B>, aGen: Arb<A>, bGen: Arb<B>, funcGen: Arb<(B) -> B>, EQA: Eq<A>, EQOptionB: Eq<Option<B>>, EQListB: Eq<ListK<B>>) = listOf(
    Law("Traversal law: head option") { traversal.headOption(aGen, EQOptionB) },
    Law("Traversal law: modify get all") { traversal.modifyGetAll(aGen, funcGen, EQListB) },
    Law("Traversal law: set is idempotent") { traversal.setIdempotent(aGen, bGen, EQA) },
    Law("Traversal law: modify identity") { traversal.modifyIdentity(aGen, EQA) },
    Law("Traversal law: compose modify") { traversal.composeModify(aGen, funcGen, EQA) }
  )

  private suspend fun <A, B : Any> Traversal<A, B>.headOption(aGen: Arb<A>, EQOptionB: Eq<Option<B>>) =
    forAll(aGen) { a ->
      headOption(a)
        .equalUnderTheLaw(getAll(a).firstOrNull().toOption(), EQOptionB)
    }

  private suspend fun <A, B> Traversal<A, B>.modifyGetAll(aGen: Arb<A>, funcGen: Arb<(B) -> B>, EQListB: Eq<ListK<B>>) =
    forAll(aGen, funcGen) { a, f ->
      getAll(modify(a, f))
        .equalUnderTheLaw(getAll(a).map(f), EQListB)
    }

  private suspend fun <A, B> Traversal<A, B>.setIdempotent(aGen: Arb<A>, bGen: Arb<B>, EQA: Eq<A>) =
    forAll(aGen, bGen) { a, b ->
      set(set(a, b), b)
        .equalUnderTheLaw(set(a, b), EQA)
    }

  private suspend fun <A, B> Traversal<A, B>.modifyIdentity(aGen: Arb<A>, EQA: Eq<A>) =
    forAll(aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, EQA)
    }

  private suspend fun <A, B> Traversal<A, B>.composeModify(aGen: Arb<A>, funcGen: Arb<(B) -> B>, EQA: Eq<A>) =
    forAll(aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g)
        .equalUnderTheLaw(modify(a, g compose f), EQA)
    }
}
