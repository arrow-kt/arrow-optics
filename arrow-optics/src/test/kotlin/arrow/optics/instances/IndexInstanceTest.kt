package arrow.optics.instances

import arrow.core.extensions.eq
import arrow.core.SequenceK
import arrow.core.extensions.sequencek.eq.eq
import arrow.optics.extensions.sequencek.index.index
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.sequenceK
import arrow.optics.test.laws.OptionalLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb

class IndexInstanceTest : UnitSpec() {

  init {

    testLaws(
      OptionalLaws.laws(
        optionalGen = Arb.int().map { SequenceK.index<String>().index(it) },
        aGen = Arb.sequenceK(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQOptionB = Eq.any(),
        EQA = SequenceK.eq(String.eq())
      )
    )
  }
}
