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
import io.kotest.property.Arb

class TupleTest : UnitSpec() {

  init {

    testLaws(
      LensLaws.laws(
        lens = Tuple2.first(),
        aGen = Arb.tuple2(Arb.int(), Arb.string()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Int.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple2.second(),
        aGen = Arb.tuple2(Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple3.first(),
        aGen = Arb.tuple3(Arb.int(), Arb.string(), Arb.string()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Int.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple3.second(),
        aGen = Arb.tuple3(Arb.int(), Arb.string(), Arb.int()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple3.third(),
        aGen = Arb.tuple3(Arb.int(), Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple2.traversal(),
        aGen = Arb.tuple2(Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple3.traversal(),
        aGen = Arb.tuple3(Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple4.traversal(),
        aGen = Arb.tuple4(Arb.int(), Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple5.traversal(),
        aGen = Arb.tuple5(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple6.traversal(),
        aGen = Arb.tuple6(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple7.traversal(),
        aGen = Arb.tuple7(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple8.traversal(),
        aGen = Arb.tuple8(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple9.traversal(),
        aGen = Arb.tuple9(
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int()
        ),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple10.traversal(),
        aGen = Arb.tuple10(
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int()
        ),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )
  }
}
