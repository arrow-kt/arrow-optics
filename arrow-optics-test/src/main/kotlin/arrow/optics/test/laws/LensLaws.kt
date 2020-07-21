package arrow.optics.test.laws

import arrow.core.Const
import arrow.core.Id
import arrow.core.compose
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.id.functor.functor
import arrow.core.identity
import arrow.core.value
import arrow.optics.Lens
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.forAll

object LensLaws {

  fun <A, B> laws(
    lensGen: Arb<Lens<A, B>>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    EQA: Eq<A>,
    EQB: Eq<B>,
    MB: Monoid<B>
  ): List<Law> =
    listOf(
      Law("Lens law: get set") { lensGetSet(lensGen, aGen, EQA) },
      Law("Lens law: set get") { lensSetGet(lensGen, aGen, bGen, EQB) },
      Law("Lens law: is set idempotent") { lensSetIdempotent(lensGen, aGen, bGen, EQA) },
      Law("Lens law: modify identity") { lensModifyIdentity(lensGen, aGen, EQA) },
      Law("Lens law: compose modify") { lensComposeModify(lensGen, aGen, funcGen, EQA) },
      Law("Lens law: consistent set modify") { lensConsistentSetModify(lensGen, aGen, bGen, EQA) },
      Law("Lens law: consistent modify modify id") { lensConsistentModifyModifyId(lensGen, aGen, funcGen, EQA) },
      Law("Lens law: consistent get modify id") { lensConsistentGetModifyid(lensGen, aGen, EQB, MB) }
    )

  /**
   * Warning: Use only when a `Arb.constant()` applies
   */
  fun <A, B> laws(
    lens: Lens<A, B>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    EQA: Eq<A>,
    EQB: Eq<B>,
    MB: Monoid<B>
  ): List<Law> = laws(Arb.constant(lens), aGen, bGen, funcGen, EQA, EQB, MB)

  private suspend fun <A, B> lensGetSet(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, EQA: Eq<A>) =
    forAll(lensGen, aGen) { lens, a ->
      lens.run {
        set(a, get(a)).equalUnderTheLaw(a, EQA)
      }
    }

  private suspend fun <A, B> lensSetGet(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, bGen: Arb<B>, EQB: Eq<B>) =
    forAll(lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        get(set(a, b)).equalUnderTheLaw(b, EQB)
      }
    }

  private suspend fun <A, B> lensSetIdempotent(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, bGen: Arb<B>, EQA: Eq<A>) =
    forAll(lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        set(set(a, b), b).equalUnderTheLaw(set(a, b), EQA)
      }
    }

  private suspend fun <A, B> lensModifyIdentity(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, EQA: Eq<A>) =
    forAll(lensGen, aGen) { lens, a ->
      lens.run {
        modify(a, ::identity).equalUnderTheLaw(a, EQA)
      }
    }

  private suspend fun <A, B> lensComposeModify(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, funcGen: Arb<(B) -> B>, EQA: Eq<A>) =
    forAll(lensGen, aGen, funcGen, funcGen) { lens, a, f, g ->
      lens.run {
        modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), EQA)
      }
    }

  private suspend fun <A, B> lensConsistentSetModify(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, bGen: Arb<B>, EQA: Eq<A>) =
    forAll(lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        set(a, b).equalUnderTheLaw(modify(a) { b }, EQA)
      }
    }

  private suspend fun <A, B> lensConsistentModifyModifyId(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, funcGen: Arb<(B) -> B>, EQA: Eq<A>) =
    forAll(lensGen, aGen, funcGen) { lens, a, f ->
      lens.run {
        modify(a, f)
          .equalUnderTheLaw(modifyF(Id.functor(), a) { Id.just(f(it)) }.value(), EQA)
      }
    }

  private suspend fun <A, B> lensConsistentGetModifyid(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, EQB: Eq<B>, MA: Monoid<B>) =
    forAll(lensGen, aGen) { lens, a ->
      lens.run {
        get(a)
          .equalUnderTheLaw(modifyF(Const.applicative(MA), a, ::Const).value(), EQB)
      }
    }
}
