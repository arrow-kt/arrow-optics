package arrow.optics.instances

import arrow.core.Option
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.optics.extensions.nonemptylist.each.each
import arrow.optics.extensions.nonemptylist.filterIndex.filterIndex
import arrow.optics.extensions.nonemptylist.index.index
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.nonEmptyList
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb

class NonEmptyListInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = NonEmptyList.each<String>().each(),
        aGen = Arb.nonEmptyList(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQA = Eq.any(),
        EQOptionB = Eq.any(),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = NonEmptyList.filterIndex<String>().filter { true },
        aGen = Arb.nonEmptyList(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Arb.int().map { NonEmptyList.index<String>().index(it) },
        aGen = Arb.nonEmptyList(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )
  }
}
