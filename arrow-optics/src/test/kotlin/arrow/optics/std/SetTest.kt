package arrow.optics.std

import arrow.core.SetExtensions
import arrow.core.SetK
import arrow.core.extensions.setk.monoid.monoid
import arrow.optics.toSetK
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.genSetK
import arrow.optics.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb

class SetTest : UnitSpec() {

  init {

    testLaws(IsoLaws.laws(
      iso = SetExtensions.toSetK(),
      aGen = Arb.set(Arb.int()),
      bGen = Arb.genSetK(Arb.int()),
      funcGen = Arb.functionAToB(Arb.genSetK(Arb.int())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = SetK.monoid()
    ))
  }
}
