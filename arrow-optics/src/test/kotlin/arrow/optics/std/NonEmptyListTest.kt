package arrow.optics.std

import arrow.core.NonEmptyList
import arrow.core.extensions.monoid
import arrow.optics.head
import arrow.optics.tail
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.nonEmptyList
import arrow.optics.test.laws.LensLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotest.property.Arb

class NonEmptyListTest : UnitSpec() {

  init {

    testLaws(
      LensLaws.laws(
        lens = NonEmptyList.head(),
        aGen = Arb.nonEmptyList(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(LensLaws.laws(
      lens = NonEmptyList.tail(),
      aGen = Arb.nonEmptyList(Arb.string()),
      bGen = Arb.list(Arb.string()),
      funcGen = Arb.functionAToB(Arb.list(Arb.string())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = object : Monoid<List<String>> {
        override fun empty(): List<String> = emptyList()
        override fun List<String>.combine(b: List<String>): List<String> = this + b
      }
    ))
  }
}
