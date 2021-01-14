package arrow.optics.instances

import arrow.core.test.UnitSpec

class MapInstanceTest : UnitSpec() {

  init {

//    testLaws(
//      TraversalLaws.laws(
//        traversal = MapK.traversal(),
//        aGen = Gen.mapK(Gen.int(), Gen.string()),
//        bGen = Gen.string(),
//        funcGen = Gen.functionAToB(Gen.string()),
//        EQA = Eq.any(),
//        EQOptionB = Option.eq(Eq.any()),
//        EQListB = ListK.eq(Eq.any())
//      )
//    )
//
//    testLaws(
//      TraversalLaws.laws(
//        traversal = MapInstances.traversal(),
//        aGen = Gen.map(Gen.int(), Gen.string()),
//        bGen = Gen.string(),
//        funcGen = Gen.functionAToB(Gen.string()),
//        EQA = Eq.any(),
//        EQOptionB = Option.eq(Eq.any()),
//        EQListB = ListK.eq(Eq.any())
//      )
//    )
//
//    testLaws(
//      TraversalLaws.laws(
//        traversal = MapK.filterIndex<Char, Int>().filter { true },
//        aGen = Gen.mapK(Gen.char(), Gen.intSmall()),
//        bGen = Gen.int(),
//        funcGen = Gen.functionAToB(Gen.int()),
//        EQA = Eq.any(),
//        EQOptionB = Option.eq(Eq.any()),
//        EQListB = ListK.eq(Eq.any())
//      )
//    )
//
//    testLaws(
//      TraversalLaws.laws(
//        traversal = MapInstances.filterIndex<Char, Int>().filter { true },
//        aGen = Gen.map(Gen.char(), Gen.intSmall()),
//        bGen = Gen.int(),
//        funcGen = Gen.functionAToB(Gen.int()),
//        EQA = Eq.any(),
//        EQOptionB = Option.eq(Eq.any()),
//        EQListB = ListK.eq(Eq.any())
//      )
//    )
//
//    testLaws(
//      OptionalLaws.laws(
//        optionalGen = Gen.string().map { MapK.index<String, Int>().index(it) },
//        aGen = Gen.mapK(Gen.string(), Gen.int()),
//        bGen = Gen.int(),
//        funcGen = Gen.functionAToB(Gen.int()),
//        EQOptionB = Eq.any(),
//        EQA = Eq.any()
//      )
//    )
//
//    testLaws(
//      OptionalLaws.laws(
//        optionalGen = Gen.string().map { MapInstances.index<String, Int>().index(it) },
//        aGen = Gen.map(Gen.string(), Gen.int()),
//        bGen = Gen.int(),
//        funcGen = Gen.functionAToB(Gen.int()),
//        EQOptionB = Eq.any(),
//        EQA = Eq.any()
//      )
//    )
//
//    testLaws(
//      LensLaws.laws(
//        lensGen = Gen.string().map { MapK.at<String, Int>().at(it) },
//        aGen = Gen.mapK(Gen.string(), Gen.int()),
//        bGen = Gen.option(Gen.int()),
//        funcGen = Gen.functionAToB(Gen.option(Gen.int())),
//        EQA = Eq.any(),
//        EQB = Eq.any(),
//        MB = Option.monoid(Int.monoid())
//      )
//    )
//
//    testLaws(
//      LensLaws.laws(
//        lensGen = Gen.string().map { MapInstances.at<String, Int>().at(it) },
//        aGen = Gen.map(Gen.string(), Gen.int()),
//        bGen = Gen.option(Gen.int()),
//        funcGen = Gen.functionAToB(Gen.option(Gen.int())),
//        EQA = Eq.any(),
//        EQB = Eq.any(),
//        MB = Option.monoid(Int.semigroup())
//      )
//    )
  }
}
