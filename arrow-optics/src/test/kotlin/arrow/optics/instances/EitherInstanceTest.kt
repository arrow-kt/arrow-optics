package arrow.optics.instances

import arrow.core.Either
import arrow.core.Option
import arrow.core.ListK
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.optics.extensions.either.each.each
import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb

class EitherInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = Either.each<String, Int>().each(),
      aGen = Arb.either(Arb.string(), Arb.int()),
      bGen = Arb.int(),
      funcGen = Arb.functionAToB(Arb.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))
  }
}
