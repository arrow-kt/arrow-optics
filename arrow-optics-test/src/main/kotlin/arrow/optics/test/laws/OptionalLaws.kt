package arrow.optics.test.laws

import arrow.core.Const
import arrow.core.Id
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.compose
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.id.applicative.applicative
import arrow.core.identity
import arrow.core.value
import arrow.optics.Optional
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.forAll

object OptionalLaws {

  fun <A, B> laws(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    EQA: Eq<A>,
    EQOptionB: Eq<Option<B>>
  ): List<Law> = listOf(
    Law("Optional Law: set what you get") { getOptionSet(optionalGen, aGen, EQA) },
    Law("Optional Law: set what you get") { setGetOption(optionalGen, aGen, bGen, EQOptionB) },
    Law("Optional Law: set is idempotent") { setIdempotent(optionalGen, aGen, bGen, EQA) },
    Law("Optional Law: modify identity = identity") { modifyIdentity(optionalGen, aGen, EQA) },
    Law("Optional Law: compose modify") { composeModify(optionalGen, aGen, funcGen, EQA) },
    Law("Optional Law: consistent set with modify") { consistentSetModify(optionalGen, aGen, bGen, EQA) },
    Law("Optional Law: consistent modify with modify identity") {
      consistentModifyModifyId(
        optionalGen,
        aGen,
        funcGen,
        EQA
      )
    },
    Law("Optional Law: consistent getOption with modify identity") {
      consistentGetOptionModifyId(
        optionalGen,
        aGen,
        EQOptionB
      )
    }
  )

  /**
   * Warning: Use only when a `Arb.constant()` applies
   */
  fun <A, B> laws(
    optional: Optional<A, B>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    EQA: Eq<A>,
    EQOptionB: Eq<Option<B>>
  ): List<Law> = laws(Arb.constant(optional), aGen, bGen, funcGen, EQA, EQOptionB)

  private suspend fun <A, B> getOptionSet(optionalGen: Arb<Optional<A, B>>, aGen: Arb<A>, EQA: Eq<A>) =
    forAll(optionalGen, aGen) { optional, a ->
      optional.run {
        getOrModify(a).fold(::identity) { set(a, it) }
          .equalUnderTheLaw(a, EQA)
      }
    }

  private suspend fun <A, B> setGetOption(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    EQOptionB: Eq<Option<B>>
  ) =
    forAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        getOption(set(a, b))
          .equalUnderTheLaw(getOption(a).map { b }, EQOptionB)
      }
    }

  private suspend fun <A, B> setIdempotent(optionalGen: Arb<Optional<A, B>>, aGen: Arb<A>, bGen: Arb<B>, EQA: Eq<A>) =
    forAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        set(set(a, b), b)
          .equalUnderTheLaw(set(a, b), EQA)
      }
    }

  private suspend fun <A, B> modifyIdentity(optionalGen: Arb<Optional<A, B>>, aGen: Arb<A>, EQA: Eq<A>) =
    forAll(optionalGen, aGen) { optional, a ->
      optional.run {
        modify(a, ::identity)
          .equalUnderTheLaw(a, EQA)
      }
    }

  private suspend fun <A, B> composeModify(optionalGen: Arb<Optional<A, B>>, aGen: Arb<A>, funcGen: Arb<(B) -> B>, EQA: Eq<A>) =
    forAll(optionalGen, aGen, funcGen, funcGen) { optional, a, f, g ->
      optional.run {
        modify(modify(a, f), g)
          .equalUnderTheLaw(modify(a, g compose f), EQA)
      }
    }

  private suspend fun <A, B> consistentSetModify(optionalGen: Arb<Optional<A, B>>, aGen: Arb<A>, bGen: Arb<B>, EQA: Eq<A>) =
    forAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        set(a, b)
          .equalUnderTheLaw(modify(a) { b }, EQA)
      }
    }

  private suspend fun <A, B> consistentModifyModifyId(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    funcGen: Arb<(B) -> B>,
    EQA: Eq<A>
  ) =
    forAll(optionalGen, aGen, funcGen) { optional, a, f ->
      optional.run {
        modify(a, f)
          .equalUnderTheLaw(modifyF(Id.applicative(), a) { Id.just(f(it)) }.value(), EQA)
      }
    }

  private suspend fun <A, B> consistentGetOptionModifyId(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    EQOptionB: Eq<Option<B>>
  ) {
    val firstMonoid = object : Monoid<FirstOption<B>> {
      override fun empty(): FirstOption<B> = FirstOption(None)
      override fun FirstOption<B>.combine(b: FirstOption<B>): FirstOption<B> =
        if (option.fold({ false }, { true })) this else b
    }

    forAll(optionalGen, aGen) { optional, a ->
      optional.run {
        modifyF(Const.applicative(firstMonoid), a) { b ->
          Const(FirstOption(Some(b)))
        }.value().option.equalUnderTheLaw(getOption(a), EQOptionB)
      }
    }
  }

  @PublishedApi
  internal data class FirstOption<A>(val option: Option<A>)
}
