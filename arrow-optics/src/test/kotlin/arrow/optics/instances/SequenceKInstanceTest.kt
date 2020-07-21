package arrow.optics.instances

import arrow.core.Option
import arrow.core.extensions.eq
import arrow.core.ListK
import arrow.core.SequenceK
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.sequencek.eq.eq
import arrow.optics.extensions.sequencek.each.each
import arrow.optics.extensions.sequencek.filterIndex.filterIndex
import arrow.optics.extensions.sequencek.index.index
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.sequenceK
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import io.kotest.property.Arb

class SequenceKInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = SequenceK.each<String>().each(),
        aGen = Arb.sequenceK(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQA = SequenceK.eq(String.eq()),
        EQOptionB = Option.eq(String.eq()),
        EQListB = ListK.eq(String.eq())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = SequenceK.filterIndex<String>().filter { true },
        aGen = Arb.sequenceK(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQA = SequenceK.eq(String.eq()),
        EQListB = ListK.eq(String.eq()),
        EQOptionB = Option.eq(String.eq())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Arb.int().map { SequenceK.index<String>().index(it) },
        aGen = Arb.sequenceK(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQOptionB = Option.eq(String.eq()),
        EQA = SequenceK.eq(String.eq())
      )
    )
  }
}
