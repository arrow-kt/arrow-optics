package arrow.optics.instances

import arrow.core.ListK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.listEq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.listK
import arrow.core.test.generators.tuple2
import arrow.optics.cons
import arrow.optics.extensions.listk.cons.cons
import arrow.optics.extensions.listk.each.each
import arrow.optics.extensions.listk.filterIndex.filterIndex
import arrow.optics.extensions.listk.index.index
import arrow.optics.extensions.listk.snoc.snoc
import arrow.optics.filterIndex
import arrow.optics.index
import arrow.optics.snoc
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.traversal
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class ListInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = ListK.each<String>().each(),
        aGen = Gen.listK(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = List::class.traversal(),
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
        traversal = ListK.filterIndex<String>().filter { true },
        aGen = Gen.listK(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQListB = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = List::class.filterIndex<String>().filter { true },
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
        optionalGen = Gen.int().map { ListK.index<String>().index(it) },
        aGen = Gen.listK(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { List::class.index<String>().index(it) },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = ListK.cons<Int>().cons(),
        aGen = Gen.listK(Gen.int()),
        bGen = Gen.tuple2(Gen.int(), Gen.listK(Gen.int())),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.int(), Gen.listK(Gen.int()))),
        EQA = ListK.eq(Int.eq()),
        EQOptionB = Option.eq(Tuple2.eq(Int.eq(), ListK.eq(Int.eq())))
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = List::class.cons<Int>().cons(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.tuple2(Gen.int(), Gen.list(Gen.int())),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.int(), Gen.list(Gen.int()))),
        EQA = listEq(Int.eq()),
        EQOptionB = Option.eq(Tuple2.eq(Int.eq(), listEq(Int.eq())))
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = ListK.snoc<Int>().snoc(),
        aGen = Gen.listK(Gen.int()),
        bGen = Gen.tuple2(Gen.listK(Gen.int()), Gen.int()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.listK(Gen.int()), Gen.int())),
        EQA = ListK.eq(Int.eq()),
        EQOptionB = Option.eq(Tuple2.eq(ListK.eq(Int.eq()), Int.eq()))
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = List::class.snoc<Int>().snoc(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.tuple2(Gen.list(Gen.int()), Gen.int()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.list(Gen.int()), Gen.int())),
        EQA = listEq(Int.eq()),
        EQOptionB = Option.eq(Tuple2.eq(listEq(Int.eq()), Int.eq()))
      )
    )
  }
}
