package arrow.optics

import arrow.Kind
import arrow.core.Option
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.core.toOption
import arrow.core.toT
import arrow.core.ForListK
import arrow.core.ListK
import arrow.mtl.State
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.listk.traverse.traverse
import arrow.core.fix
import arrow.core.k
import arrow.mtl.map
import arrow.mtl.run
import arrow.optics.mtl.assign
import arrow.optics.mtl.assignOld
import arrow.optics.mtl.assign_
import arrow.optics.mtl.extract
import arrow.optics.mtl.extractMap
import arrow.optics.mtl.toState
import arrow.optics.mtl.update
import arrow.optics.mtl.updateOld
import arrow.optics.mtl.update_
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.listK
import arrow.core.test.generators.tuple2
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll

class TraversalTest : UnitSpec() {

  init {

    val listKTraverse = Traversal.fromTraversable<ForListK, Int, Int>(ListK.traverse())

    testLaws(
      TraversalLaws.laws(
        traversal = listKTraverse,
        aGen = Arb.listK(Arb.int()) as Arb<Kind<ForListK, Int>>,
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      SetterLaws.laws(
        setter = listKTraverse.asSetter(),
        aGen = Arb.listK(Arb.int()) as Arb<Kind<ForListK, Int>>,
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        EQA = ListK.eq(Eq.any())
      )
    )

    testLaws(TraversalLaws.laws(
      traversal = Traversal({ it.a }, { it.b }, { a, b, _ -> a toT b }),
      aGen = Arb.tuple2(Arb.float(), Arb.float()),
      bGen = Arb.float(),
      funcGen = Arb.functionAToB(Arb.float()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    with(listKTraverse.asFold()) {

      "asFold should behave as valid Fold: size" {
        forAll(Arb.listK(Arb.int())) { ints ->
          size(ints) == ints.size
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(Arb.listK(Arb.int())) { ints ->
          nonEmpty(ints) == ints.isNotEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(Arb.listK(Arb.int())) { ints ->
          isEmpty(ints) == ints.isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(Arb.listK(Arb.int())) { ints ->
          getAll(ints) == ints.k()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(Arb.listK(Arb.int())) { ints ->
          combineAll(Int.monoid(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(Arb.listK(Arb.int())) { ints ->
          fold(Int.monoid(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(Arb.listK(Arb.int())) { ints ->
          headOption(ints) == ints.firstOrNull().toOption()
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(Arb.listK(Arb.int())) { ints ->
          lastOption(ints) == ints.lastOrNull().toOption()
        }
      }
    }

    with(listKTraverse) {

      "Getting all targets of a traversal" {
        forAll(Arb.list(Arb.int())) { ints ->
          getAll(ints.k()) == ints.k()
        }
      }

      "Folding all the values of a traversal" {
        forAll(Arb.list(Arb.int())) { ints ->
          fold(Int.monoid(), ints.k()) == ints.sum()
        }
      }

      "Combining all the values of a traversal" {
        forAll(Arb.list(Arb.int())) { ints ->
          combineAll(Int.monoid(), ints.k()) == ints.sum()
        }
      }

      "Finding an number larger than 10" {
        forAll(Arb.list(Arb.int(-100, 100))) { ints ->
          find(ints.k()) { it > 10 } == Option.fromNullable(ints.firstOrNull { it > 10 })
        }
      }

      "Get the length from a traversal" {
        forAll(Arb.list(Arb.int())) { ints ->
          size(ints.k()) == ints.size
        }
      }

      "Extract should extract the focus from the state" {
        forAll(Arb.listK(Arb.int())) { ints ->
          extract().run(ints) ==
            State { iis: ListK<Int> ->
              iis toT getAll(iis)
            }.run(ints)
        }
      }

      "toState should be an alias to extract" {
        forAll(Arb.listK(Arb.int())) { ints ->
          toState().run(ints) == extract().run(ints)
        }
      }

      "Extracts with f should be same as extract and map" {
        forAll(Arb.listK(Arb.int()), Arb.functionAToB<Int, String>(Arb.string())) { ints, f ->
          extractMap(f).run(ints) == extract().map { it.map(f) }.run(ints)
        }
      }

      "update f should be same modify f within State and returning new state" {
        forAll(Arb.listK(Arb.int()), Arb.functionAToB<Int, Int>(Arb.int())) { ints, f ->
          update(f).run(ints) ==
            State { iis: ListK<Int> ->
              modify(iis, f)
                .let { it.fix() toT getAll(it) }
            }.run(ints)
        }
      }

      "updateOld f should be same as modify f within State and returning old state" {
        forAll(Arb.listK(Arb.int()), Arb.functionAToB<Int, Int>(Arb.int())) { ints, f ->
          updateOld(f).run(ints) ==
            State { iis: ListK<Int> ->
              modify(iis, f).fix() toT getAll(iis)
            }.run(ints)
        }
      }

      "update_ f should be as modify f within State and returning Unit" {
        forAll(Arb.listK(Arb.int()), Arb.functionAToB<Int, Int>(Arb.int())) { ints, f ->
          update_(f).run(ints) ==
            State { iis: ListK<Int> ->
              modify(iis, f).fix() toT Unit
            }.run(ints)
        }
      }

      "assign a should be same set a within State and returning new value" {
        forAll(Arb.listK(Arb.int()), Arb.int()) { ints, i ->
          assign(i).run(ints) ==
            State { iis: ListK<Int> ->
              set(iis, i)
                .let { it.fix() toT getAll(it) }
            }.run(ints)
        }
      }

      "assignOld f should be same as modify f within State and returning old state" {
        forAll(Arb.listK(Arb.int()), Arb.int()) { ints, i ->
          assignOld(i).run(ints) ==
            State { iis: ListK<Int> ->
              set(iis, i).fix() toT getAll(iis)
            }.run(ints)
        }
      }

      "assign_ f should be as modify f within State and returning Unit" {
        forAll(Arb.listK(Arb.int()), Arb.int()) { ints, i ->
          assign_(i).run(ints) ==
            State { iis: ListK<Int> ->
              set(iis, i).fix() toT Unit
            }.run(ints)
        }
      }
    }
  }
}
