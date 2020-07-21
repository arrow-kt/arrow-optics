package arrow.optics.instances

import arrow.core.MapInstances
import arrow.core.Option
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.monoid.monoid
import arrow.core.extensions.semigroup
import arrow.core.ListK
import arrow.core.MapK
import arrow.core.extensions.listk.eq.eq
import arrow.optics.extensions.at
import arrow.optics.extensions.each
import arrow.optics.extensions.filterIndex
import arrow.optics.extensions.index
import arrow.optics.extensions.mapk.at.at
import arrow.optics.extensions.mapk.each.each
import arrow.optics.extensions.mapk.filterIndex.filterIndex
import arrow.optics.extensions.mapk.index.index
import arrow.optics.test.generators.char
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.mapK
import arrow.core.test.generators.option
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb

class MapInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = MapK.each<Int, String>().each(),
        aGen = Arb.mapK(Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = MapInstances.each<Int, String>().each(),
        aGen = Arb.map(Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = MapK.filterIndex<Char, Int>().filter { true },
        aGen = Arb.mapK(Arb.char(), Arb.intSmall()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = MapInstances.filterIndex<Char, Int>().filter { true },
        aGen = Arb.map(Arb.char(), Arb.intSmall()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Arb.string().map { MapK.index<String, Int>().index(it) },
        aGen = Arb.mapK(Arb.string(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Arb.string().map { MapInstances.index<String, Int>().index(it) },
        aGen = Arb.map(Arb.string(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      LensLaws.laws(
        lensGen = Arb.string().map { MapK.at<String, Int>().at(it) },
        aGen = Arb.mapK(Arb.string(), Arb.int()),
        bGen = Arb.option(Arb.int()),
        funcGen = Arb.functionAToB(Arb.option(Arb.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Option.monoid(Int.monoid())
      )
    )

    testLaws(
      LensLaws.laws(
        lensGen = Arb.string().map { MapInstances.at<String, Int>().at(it) },
        aGen = Arb.map(Arb.string(), Arb.int()),
        bGen = Arb.option(Arb.int()),
        funcGen = Arb.functionAToB(Arb.option(Arb.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Option.monoid(Int.semigroup())
      )
    )
  }
}
