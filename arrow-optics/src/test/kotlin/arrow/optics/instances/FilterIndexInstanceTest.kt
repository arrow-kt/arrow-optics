package arrow.optics.instances

import arrow.core.Option
import arrow.core.extensions.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.ListK
import arrow.core.MapK
import arrow.core.NonEmptyList
import arrow.core.SequenceK
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.sequencek.eq.eq
import arrow.optics.extensions.ListFilterIndex
import arrow.optics.extensions.filterMapIndex
import arrow.optics.extensions.filterIndex
import arrow.optics.extensions.listk.filterIndex.filterIndex
import arrow.optics.extensions.mapk.filterIndex.filterIndex
import arrow.optics.extensions.nonemptylist.filterIndex.filterIndex
import arrow.optics.extensions.sequencek.filterIndex.filterIndex
import arrow.optics.test.generators.char
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.listK
import arrow.core.test.generators.mapK
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.sequenceK
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb

class FilterIndexInstanceTest : UnitSpec() {

  init {
    testLaws(TraversalLaws.laws(
      traversal = ListK.filterIndex<String>().filter { true },
      aGen = Arb.listK(Arb.string()),
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string()),
      EQA = Eq.any(),
      EQListB = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(TraversalLaws.laws(
      traversal = ListFilterIndex<String>().filter { true },
      aGen = Arb.list(Arb.string()),
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string()),
      EQA = Eq.any(),
      EQListB = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(TraversalLaws.laws(
      traversal = NonEmptyList.filterIndex<String>().filter { true },
      aGen = Arb.nonEmptyList(Arb.string()),
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = SequenceK.filterIndex<Char>().filter { true },
      aGen = Arb.sequenceK(Arb.char()),
      bGen = Arb.char(),
      funcGen = Arb.functionAToB(Arb.char()),
      EQA = SequenceK.eq(Char.eq()),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = MapK.filterIndex<Char, Int>().filter { true },
      aGen = Arb.mapK(Arb.char(), Arb.intSmall()),
      bGen = Arb.int(),
      funcGen = Arb.functionAToB(Arb.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = filterMapIndex<Char, Int>().filter { true },
      aGen = Arb.map(Arb.char(), Arb.intSmall()),
      bGen = Arb.int(),
      funcGen = Arb.functionAToB(Arb.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = String.filterIndex().filter { true },
      aGen = Arb.string(),
      bGen = Arb.char(),
      funcGen = Arb.functionAToB(Arb.char()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))
  }
}
