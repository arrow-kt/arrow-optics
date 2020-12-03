package arrow.optics.instances

import arrow.core.Option
import arrow.core.extensions.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.extensions.each
import arrow.optics.extensions.eq
import arrow.optics.extensions.filterIndex
import arrow.optics.extensions.index
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import io.kotlintest.properties.Gen

class SequenceKInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Sequence::class.each<String>().each(),
        aGen = Gen.list(Gen.string()).map { it.asSequence() }, // TODO: Create generator in Arrow Core
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Sequence::class.eq(String.eq()),
        EQOptionB = Option.eq(String.eq()),
        EQListB = List::class.eq(String.eq())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Sequence::class.filterIndex<String>().filter { true },
        aGen = Gen.list(Gen.string()).map { it.asSequence() },
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Sequence::class.eq(String.eq()),
        EQListB = List::class.eq(String.eq()),
        EQOptionB = Option.eq(String.eq())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { Sequence::class.index<String>().index(it) },
        aGen = Gen.list(Gen.string()).map { it.asSequence() },
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Option.eq(String.eq()),
        EQA = Sequence::class.eq(String.eq())
      )
    )
  }
}
