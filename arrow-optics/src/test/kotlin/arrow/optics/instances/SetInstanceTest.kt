package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.typeclasses.Monoid

class SetInstanceTest : UnitSpec() {

  object AndMonoid : Monoid<Boolean> {
    override fun Boolean.combine(b: Boolean): Boolean = this && b
    override fun empty(): Boolean = true
  }

  init {

//    testLaws(
//      LensLaws.laws(
//        lensGen = Gen.string().map { SetK.at<String>().at(it) },
//        aGen = Gen.genSetK(Gen.string()),
//        bGen = Gen.bool(),
//        funcGen = Gen.functionAToB(Gen.bool()),
//        EQA = SetK.eq(String.eq()),
//        EQB = Eq.any(),
//        MB = AndMonoid
//      )
//    )
//
//    testLaws(
//      LensLaws.laws(
//        lensGen = Gen.string().map { setAt<String>().at(it) },
//        aGen = Gen.set(Gen.string()),
//        bGen = Gen.bool(),
//        funcGen = Gen.functionAToB(Gen.bool()),
//        EQA = Eq.any(),
//        EQB = Eq.any(),
//        MB = AndMonoid
//      )
//    )
  }
}
