package arrow.optics

import arrow.core.Option
import arrow.core.extensions.int
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.toT
import arrow.mtl.State
import arrow.mtl.map
import arrow.mtl.run
import arrow.optics.mtl.extract
import arrow.optics.mtl.toState
import arrow.optics.mtl.assign
import arrow.optics.mtl.assign_
import arrow.optics.mtl.update
import arrow.optics.mtl.assignOld
import arrow.optics.mtl.extractMap
import arrow.optics.mtl.updateOld
import arrow.optics.mtl.update_
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class EveryTest : UnitSpec() {
  init {

    with(Every.list<Int>().asFold()) {

      "asFold should behave as valid Fold: size" {
        forAll(Gen.list(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(Gen.list(Gen.int())) { ints ->
          nonEmpty(ints) == ints.isNotEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(Gen.list(Gen.int())) { ints ->
          isEmpty(ints) == ints.isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(Gen.list(Gen.int())) { ints ->
          getAll(ints) == ints
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(Gen.list(Gen.int())) { ints ->
          combineAll(Monoid.int(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(Gen.list(Gen.int())) { ints ->
          fold(Monoid.int(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(Gen.list(Gen.int())) { ints ->
          headOption(ints) == Option.fromNullable(ints.firstOrNull())
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(Gen.list(Gen.int())) { ints ->
          lastOption(ints) == Option.fromNullable(ints.lastOrNull())
        }
      }
    }

    with(Every.list<Int>()) {

      "Getting all targets of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          getAll(ints) == ints
        }
      }

      "Folding all the values of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          fold(Monoid.int(), ints) == ints.sum()
        }
      }

      "Combining all the values of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          combineAll(Monoid.int(), ints) == ints.sum()
        }
      }

      "Finding an number larger than 10" {
        forAll(Gen.list(Gen.choose(-100, 100))) { ints ->
          find(ints) { it > 10 } == Option.fromNullable(ints.firstOrNull { it > 10 })
        }
      }

      "Get the length from a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }

      "Extract should extract the focus from the state" {
        forAll(Gen.list(Gen.int())) { ints ->
          extract().run(ints) ==
            State { iis: List<Int> ->
              iis toT getAll(iis)
            }.run(ints)
        }
      }

      "toState should be an alias to extract" {
        forAll(Gen.list(Gen.int())) { ints ->
          toState().run(ints) == extract().run(ints)
        }
      }

      "Extracts with f should be same as extract and map" {
        forAll(Gen.list(Gen.int()), Gen.functionAToB<Int, String>(Gen.string())) { ints, f ->
          extractMap(f).run(ints) == extract().map { it.map(f) }.run(ints)
        }
      }

      "update f should be same modify f within State and returning new state" {
        forAll(Gen.list(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { ints, f ->
          update(f).run(ints) ==
            State { iis: List<Int> ->
              val ii = modify(iis, f)
              ii toT getAll(ii)
            }.run(ints)
        }
      }

      "updateOld f should be same as modify f within State and returning old state" {
        forAll(Gen.list(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { ints, f ->
          updateOld(f).run(ints) ==
            State { iis: List<Int> ->
              modify(iis, f) toT getAll(iis)
            }.run(ints)
        }
      }

      "update_ f should be as modify f within State and returning Unit" {
        forAll(Gen.list(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { ints, f ->
          update_(f).run(ints) ==
            State { iis: List<Int> ->
              modify(iis, f) toT Unit
            }.run(ints)
        }
      }

      "assign a should be same set a within State and returning new value" {
        forAll(Gen.list(Gen.int()), Gen.int()) { ints, i ->
          assign(i).run(ints) ==
            State { iis: List<Int> ->
              val ii = set(iis, i)
              ii toT getAll(ii)
            }.run(ints)
        }
      }

      "assignOld f should be same as modify f within State and returning old state" {
        forAll(Gen.list(Gen.int()), Gen.int()) { ints, i ->
          assignOld(i).run(ints) ==
            State { iis: List<Int> ->
              set(iis, i) toT getAll(iis)
            }.run(ints)
        }
      }

      "assign_ f should be as modify f within State and returning Unit" {
        forAll(Gen.list(Gen.int()), Gen.int()) { ints, i ->
          assign_(i).run(ints) ==
            State { iis: List<Int> ->
              set(iis, i) toT Unit
            }.run(ints)
        }
      }
    }
  }
}
