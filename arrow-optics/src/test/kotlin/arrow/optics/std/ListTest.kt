package arrow.optics.std

import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.option.monoid.monoid
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.option
import arrow.optics.head
import arrow.optics.tail
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.toOptionNel
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class ListTest : UnitSpec() {

  init {

    testLaws(
      OptionalLaws.laws(
        optional = List::class.head(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optional = List::class.tail(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.list(Gen.int()),
        funcGen = Gen.functionAToB(Gen.list(Gen.int())),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      IsoLaws.laws(
        iso = List::class.toOptionNel(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.option(Gen.nonEmptyList(Gen.int())),
        funcGen = Gen.functionAToB(Gen.option(Gen.nonEmptyList(Gen.int()))),
        EQA = Eq.any(),
        EQB = Eq.any(),
        bMonoid = Option.monoid(NonEmptyList.semigroup<Int>())
      )
    )
  }
}
