package arrow.optics.std

import arrow.core.ListK
import arrow.core.Option
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.optics.first
import arrow.optics.second
import arrow.optics.third
import arrow.optics.traversal
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.tuple10
import arrow.core.test.generators.tuple2
import arrow.core.test.generators.tuple3
import arrow.core.test.generators.tuple4
import arrow.core.test.generators.tuple5
import arrow.core.test.generators.tuple6
import arrow.core.test.generators.tuple7
import arrow.core.test.generators.tuple8
import arrow.core.test.generators.tuple9
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class TupleTest : UnitSpec() {

  init {

    testLaws(
      LensLaws.laws(
        lens = Tuple2.first(),
        aGen = Gen.tuple2(Gen.int(), Gen.string()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Int.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple2.second(),
        aGen = Gen.tuple2(Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple3.first(),
        aGen = Gen.tuple3(Gen.int(), Gen.string(), Gen.string()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Int.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple3.second(),
        aGen = Gen.tuple3(Gen.int(), Gen.string(), Gen.int()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple3.third(),
        aGen = Gen.tuple3(Gen.int(), Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple2.traversal(),
        aGen = Gen.tuple2(Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple3.traversal(),
        aGen = Gen.tuple3(Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple4.traversal(),
        aGen = Gen.tuple4(Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple5.traversal(),
        aGen = Gen.tuple5(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple6.traversal(),
        aGen = Gen.tuple6(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple7.traversal(),
        aGen = Gen.tuple7(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple8.traversal(),
        aGen = Gen.tuple8(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple9.traversal(),
        aGen = Gen.tuple9(
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int()
        ),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple10.traversal(),
        aGen = Gen.tuple10(
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int()
        ),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )
  }
}
