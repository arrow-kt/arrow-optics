package arrow.optics.instances

import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.option.eq.eq
import arrow.optics.extensions.either.each.each
import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.optics.extensions.eq
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class EitherInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Either.each<String, Int>().each(),
        aGen = Gen.either(Gen.string(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = List::class.eq(Eq.any())
      )
    )
  }
}
