package arrow.optics.instances

import arrow.core.Option
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.monoid.monoid
import arrow.core.extensions.semigroup
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.option
import arrow.optics.extensions.at
import arrow.optics.extensions.each
import arrow.optics.extensions.eq
import arrow.optics.extensions.filterIndex
import arrow.optics.extensions.index
import arrow.optics.test.generators.char
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class MapInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Map::class.each<Int, String>().each(),
        aGen = Gen.map(Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = List::class.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Map::class.each<Int, String>().each(),
        aGen = Gen.map(Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = List::class.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Map::class.filterIndex<Char, Int>().filter { true },
        aGen = Gen.map(Gen.char(), Gen.intSmall()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = List::class.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Map::class.filterIndex<Char, Int>().filter { true },
        aGen = Gen.map(Gen.char(), Gen.intSmall()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = List::class.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.string().map { Map::class.index<String, Int>().index(it) },
        aGen = Gen.map(Gen.string(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.string().map { Map::class.index<String, Int>().index(it) },
        aGen = Gen.map(Gen.string(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { Map::class.at<String, Int>().at(it) },
        aGen = Gen.map(Gen.string(), Gen.int()),
        bGen = Gen.option(Gen.int()),
        funcGen = Gen.functionAToB(Gen.option(Gen.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Option.monoid(Int.monoid())
      )
    )

    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { Map::class.at<String, Int>().at(it) },
        aGen = Gen.map(Gen.string(), Gen.int()),
        bGen = Gen.option(Gen.int()),
        funcGen = Gen.functionAToB(Gen.option(Gen.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Option.monoid(Int.semigroup())
      )
    )
  }
}
