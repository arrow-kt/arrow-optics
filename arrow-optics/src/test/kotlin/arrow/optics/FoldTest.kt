package arrow.optics

import arrow.core.Option
import arrow.core.extensions.list.foldable.nonEmpty
import arrow.core.k
import arrow.core.test.UnitSpec
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class FoldTest : UnitSpec() {

  init {

    val intFold = Fold<List<Int>, Int>()
    val stringFold = Fold<List<String>, String>()

    "Fold select a list that contains one" {
      val select = Fold.select<List<Int>> { it.contains(1) }

      forAll(Gen.list(Gen.int())) { ints ->
        select.run { getAll(ints) }.firstOrNull() ==
          ints.let { if (it.contains(1)) it else null }
      }
    }

    with(intFold) {

      "Folding a list of ints" {
        forAll(Gen.list(Gen.int())) { ints ->
          fold(ints, 0, Int::plus) == ints.sum()
        }
      }

      "Folding a list should yield same result as combineAll" {
        forAll(Gen.list(Gen.int())) { ints ->
          combineAll(ints, 0, Int::plus) == ints.sum()
        }
      }

      "Folding and mapping a list of strings" {
        forAll(Gen.list(Gen.int())) { ints ->
          stringFold.run {
            foldMap(ints.map(Int::toString), 0, Int::plus, String::toInt)
          } == ints.sum()
        }
      }

      "Get all targets" {
        forAll(Gen.list(Gen.int())) { ints ->
          getAll(ints.k()) == ints.k()
        }
      }

      "Get the size of the fold" {
        forAll(Gen.list(Gen.int())) { ints ->
          size(ints.k()) == ints.size
        }
      }

      "Find the first element matching the predicate" {
        forAll(Gen.list(Gen.choose(-100, 100))) { ints ->
          find(ints.k()) { it > 10 } == Option.fromNullable(ints.firstOrNull { it > 10 })
        }
      }

      "Checking existence of a target" {
        forAll(Gen.list(Gen.int()), Gen.bool()) { ints, predicate ->
          exists(ints.k()) { predicate } == (predicate && ints.nonEmpty())
        }
      }

      "Check if all targets match the predicate" {
        forAll(Gen.list(Gen.int())) { ints ->
          forall(ints.k()) { it % 2 == 0 } == ints.all { it % 2 == 0 }
        }
      }

      "Check if there is no target" {
        forAll(Gen.list(Gen.int())) { ints ->
          isEmpty(ints.k()) == ints.isEmpty()
        }
      }

      "Check if there is a target" {
        forAll(Gen.list(Gen.int())) { ints ->
          nonEmpty(ints.k()) == ints.isNotEmpty()
        }
      }
    }
  }
}
