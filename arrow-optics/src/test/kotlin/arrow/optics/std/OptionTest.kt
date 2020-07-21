package arrow.optics.std

import arrow.core.Either
import arrow.core.Option
import arrow.core.Right
import arrow.core.extensions.monoid
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.option.monoid.monoid
import arrow.core.fix
import arrow.optics.none
import arrow.optics.some
import arrow.optics.toEither
import arrow.optics.toNullable
import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.option
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.PrismLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotest.property.Arb

class OptionTest : UnitSpec() {

  init {

    testLaws(PrismLaws.laws(
      prism = Option.some(),
      aGen = Arb.option(Arb.int()),
      bGen = Arb.int(),
      funcGen = Arb.functionAToB(Arb.int()),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(PrismLaws.laws(
      prism = Option.none(),
      aGen = Arb.option(Arb.int()),
      bGen = Arb.create { Unit },
      funcGen = Arb.functionAToB(Arb.create { Unit }),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(IsoLaws.laws(
      iso = Option.toNullable<Int>().reverse(),
      aGen = Arb.int().orNull(),
      bGen = Arb.option(Arb.int()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      funcGen = Arb.functionAToB(Arb.option(Arb.int())),
      bMonoid = Option.monoid(Int.monoid())
    ))

    testLaws(IsoLaws.laws(
      iso = Option.toEither(),
      aGen = Arb.option(Arb.int()),
      bGen = Arb.either(Arb.create { Unit }, Arb.int()),
      funcGen = Arb.functionAToB(Arb.either(Arb.create { Unit }, Arb.int())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = object : Monoid<Either<Unit, Int>> {
        override fun Either<Unit, Int>.combine(b: Either<Unit, Int>): Either<Unit, Int> =
          Either.applicative<Unit>().run { this@combine.map2(b) { (a, b) -> a + b }.fix() }

        override fun empty(): Either<Unit, Int> = Right(0)
      }
    ))
  }
}
