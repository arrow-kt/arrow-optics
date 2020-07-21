package arrow.optics.std

import arrow.core.Id
import arrow.core.extensions.monoid
import arrow.optics.toValue
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb

class IdInstancesTest : UnitSpec() {

  init {
    testLaws(IsoLaws.laws(
      iso = Id.toValue(),
      aGen = Arb.int().map { Id(it) },
      bGen = Arb.int(),
      funcGen = Arb.functionAToB(Arb.int()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = Int.monoid()
    ))
  }
}
