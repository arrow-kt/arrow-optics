package arrow.optics.instances

import arrow.core.ListK
import arrow.core.MapInstances
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.extensions.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.listk.eq.eq
import arrow.optics.extensions.ListFilterIndex
import arrow.optics.extensions.FilterMapIndex
import arrow.optics.extensions.filterIndex
import arrow.optics.extensions.nonemptylist.filterIndex.filterIndex
import arrow.optics.test.generators.char
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.sequenceK
import arrow.optics.extensions.eq
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class FilterIndexInstanceTest : UnitSpec() {

  init {
    testLaws(
      TraversalLaws.laws(
        traversal = List::class.filterIndex<String>().filter { true },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQListB = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = ListFilterIndex<String>().filter { true },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQListB = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = NonEmptyList.filterIndex<String>().filter { true },
        aGen = Gen.nonEmptyList(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Sequence::class.filterIndex<Char>().filter { true },
        aGen = Gen.sequenceK(Gen.char()).map { it.sequence },
        bGen = Gen.char(),
        funcGen = Gen.functionAToB(Gen.char()),
        EQA = Sequence::class.eq(Char.eq()),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = MapInstances.filterIndex<Char, Int>().filter { true },
        aGen = Gen.map(Gen.char(), Gen.intSmall()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterMapIndex<Char, Int>().filter { true },
        aGen = Gen.map(Gen.char(), Gen.intSmall()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = String.filterIndex().filter { true },
        aGen = Gen.string(),
        bGen = Gen.char(),
        funcGen = Gen.functionAToB(Gen.char()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )
  }
}
