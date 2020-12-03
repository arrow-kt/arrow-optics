package arrow.optics.instances

import arrow.core.extensions.eq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.genSetK
import arrow.optics.extensions.SetAt
import arrow.optics.extensions.at
import arrow.optics.extensions.eq
import arrow.optics.test.laws.LensLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class SetInstanceTest : UnitSpec() {

  object AndMonoid : Monoid<Boolean> {
    override fun Boolean.combine(b: Boolean): Boolean = this && b
    override fun empty(): Boolean = true
  }

  init {

    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { Set::class.at<String>().at(it) },
        aGen = Gen.genSetK(Gen.string()).map { it.toSet() }, // TODO: Add genSet to Gen
        bGen = Gen.bool(),
        funcGen = Gen.functionAToB(Gen.bool()),
        EQA = Set::class.eq(String.eq()),
        EQB = Eq.any(),
        MB = AndMonoid
      )
    )

    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { SetAt<String>().at(it) },
        aGen = Gen.set(Gen.string()),
        bGen = Gen.bool(),
        funcGen = Gen.functionAToB(Gen.bool()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = AndMonoid
      )
    )
  }
}
