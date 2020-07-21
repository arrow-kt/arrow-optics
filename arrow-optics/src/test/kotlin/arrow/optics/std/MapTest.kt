package arrow.optics.std

import arrow.core.MapK
import arrow.core.SetK
import arrow.core.extensions.setk.monoid.monoid
import arrow.optics.toSetK
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.genSetK
import arrow.core.test.generators.mapK
import arrow.optics.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb

class MapTest : UnitSpec() {

  init {

    testLaws(IsoLaws.laws(
      iso = MapK.toSetK(),
      aGen = Arb.mapK(Arb.string(), Arb.create { Unit }),
      bGen = Arb.genSetK(Arb.string()),
      funcGen = Arb.functionAToB(Arb.genSetK(Arb.string())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = SetK.monoid()
    ))
  }
}
