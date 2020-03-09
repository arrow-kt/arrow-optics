package arrow.optics.std

import arrow.core.ListExtensions
import arrow.core.Option
import arrow.core.k
import arrow.core.extensions.option.monoid.monoid
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.optics.head
import arrow.optics.tail
import arrow.optics.toListK
import arrow.optics.toOptionNel
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.nonEmptyList
import arrow.test.generators.option
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class ListTest : UnitSpec() {

  init {

    testLaws(OptionalLaws.laws(
      optional = ListK.head(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = ListK.tail(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.list(Gen.int()),
      funcGen = Gen.functionAToB(Gen.list(Gen.int())),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(IsoLaws.laws(
      iso = ListK.toOptionNel(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.option(Gen.nonEmptyList(Gen.int())),
      funcGen = Gen.functionAToB(Gen.option(Gen.nonEmptyList(Gen.int()))),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = Option.monoid(NonEmptyList.semigroup<Int>())
    ))

    testLaws(IsoLaws.laws(
      iso = ListExtensions.toListK(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.list(Gen.int()).map { it.k() },
      funcGen = Gen.functionAToB(Gen.list(Gen.int()).map { it.k() }),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = ListK.monoid())
    )
  }
}
