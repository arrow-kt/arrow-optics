package arrow.optics.instances

import arrow.core.ListExtensions
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.ListK
import arrow.core.extensions.ListKEq
import arrow.core.extensions.ListKEqK
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.listK
import arrow.core.test.generators.tuple2
import arrow.optics.extensions.*
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class ListInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = ListExtensions.each<String>().each(),
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = ListExtensions.each<String>().each(),
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = ListExtensions.filterIndex<String>().filter { true },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQListB = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = ListExtensions.filterIndex<String>().filter { true },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQListB = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { ListExtensions.index<String>().index(it) },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { ListExtensions.index<String>().index(it) },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = ListExtensions.cons<Int>().cons(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.tuple2(Gen.int(), Gen.list(Gen.int())),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.int(), Gen.list(Gen.int()))),
        EQA = ListExtensions.eq(Int.eq()),
        EQOptionB = Option.eq(Tuple2.eq(Int.eq(), ListExtensions.eq(Int.eq())))
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = ListExtensions.snoc<Int>().snoc(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.tuple2(Gen.list(Gen.int()), Gen.int()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.list(Gen.int()), Gen.int())),
        EQA = ListExtensions.eq(Int.eq()),
        EQOptionB = Option.eq(Tuple2.eq(ListExtensions.eq(Int.eq()), Int.eq()))
      )
    )
  }
}
