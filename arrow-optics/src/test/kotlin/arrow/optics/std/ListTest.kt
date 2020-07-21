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
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.option
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb

class ListTest : UnitSpec() {

  init {

    testLaws(OptionalLaws.laws(
      optional = ListK.head(),
      aGen = Arb.list(Arb.int()),
      bGen = Arb.int(),
      funcGen = Arb.functionAToB(Arb.int()),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = ListK.tail(),
      aGen = Arb.list(Arb.int()),
      bGen = Arb.list(Arb.int()),
      funcGen = Arb.functionAToB(Arb.list(Arb.int())),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(IsoLaws.laws(
      iso = ListK.toOptionNel(),
      aGen = Arb.list(Arb.int()),
      bGen = Arb.option(Arb.nonEmptyList(Arb.int())),
      funcGen = Arb.functionAToB(Arb.option(Arb.nonEmptyList(Arb.int()))),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = Option.monoid(NonEmptyList.semigroup<Int>())
    ))

    testLaws(IsoLaws.laws(
      iso = ListExtensions.toListK(),
      aGen = Arb.list(Arb.int()),
      bGen = Arb.list(Arb.int()).map { it.k() },
      funcGen = Arb.functionAToB(Arb.list(Arb.int()).map { it.k() }),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = ListK.monoid())
    )
  }
}
