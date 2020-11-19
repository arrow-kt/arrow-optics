package arrow.optics.instances

import arrow.core.extensions.eq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.sequenceK
import arrow.optics.extensions.eq
import arrow.optics.extensions.index
import arrow.optics.test.laws.OptionalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class IndexInstanceTest : UnitSpec() {

  init {

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { Sequence::class.index<String>().index(it) },
        aGen = Gen.sequenceK(Gen.string()).map { it.sequence }, // TODO: create generator in Arrow Core
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Sequence::class.eq(String.eq())
      )
    )
  }
}
