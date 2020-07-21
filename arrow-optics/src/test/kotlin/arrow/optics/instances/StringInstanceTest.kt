package arrow.optics.instances

import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.ListK
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.optics.extensions.cons
import arrow.optics.extensions.each
import arrow.optics.extensions.filterIndex
import arrow.optics.extensions.index
import arrow.optics.extensions.snoc
import arrow.optics.test.generators.char
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.tuple2
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb

class StringInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = String.each().each(),
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = String.filterIndex().filter { true },
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Arb.int().map { String.index().index(it) },
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = String.cons().cons(),
        aGen = Arb.string(),
        bGen = Arb.tuple2(Arb.char(), Arb.string()),
        funcGen = Arb.functionAToB(Arb.tuple2(Arb.char(), Arb.string())),
        EQA = String.eq(),
        EQOptionB = Option.eq(Tuple2.eq(Char.eq(), String.eq()))
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = String.snoc().snoc(),
        aGen = Arb.string(),
        bGen = Arb.tuple2(Arb.string(), Arb.char()),
        funcGen = Arb.functionAToB(Arb.tuple2(Arb.string(), Arb.char())),
        EQA = String.eq(),
        EQOptionB = Option.eq(Tuple2.eq(String.eq(), Char.eq()))
      )
    )
  }
}
