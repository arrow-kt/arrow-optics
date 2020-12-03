package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.extensions.monoid
import arrow.optics.test.laws.IsoLaws
import arrow.optics.toSet
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class MapTest : UnitSpec() {

  init {

    testLaws(
      IsoLaws.laws(
        iso = Map::class.toSet<String>(),
        aGen = Gen.map(Gen.string(), Gen.create { Unit }),
        bGen = Gen.set(Gen.string()),
        funcGen = Gen.functionAToB(Gen.set(Gen.string())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        bMonoid = Set::class.monoid()
      )
    )
  }
}
