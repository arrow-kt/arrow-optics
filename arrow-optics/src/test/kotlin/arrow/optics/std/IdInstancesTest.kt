package arrow.optics.std

import arrow.core.Id
import arrow.core.extensions.monoid
import arrow.optics.toValue
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class IdInstancesTest : UnitSpec() {

  init {
    testLaws(IsoLaws.laws(
      iso = Id.toValue(),
      aGen = Gen.int().map { Id(it) },
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = Int.monoid()
    ))
  }
}
