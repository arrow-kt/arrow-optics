package arrow.optics

import arrow.core.Option
import arrow.core.extensions.monoid
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.extensions.list.foldable.nonEmpty
import arrow.core.extensions.listk.foldable.foldable
import arrow.core.k
import arrow.core.test.UnitSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.forAll

class FoldTest : UnitSpec() {

  init {

    val intFold = Fold.fromFoldable<ForListK, Int>(ListK.foldable())
    val stringFold = Fold.fromFoldable<ForListK, String>(ListK.foldable())

    "Fold select a list that contains one" {
      val select = Fold.select<List<Int>> { it.contains(1) }

      forAll(Arb.list(Arb.int())) { ints ->
        select.run { getAll(ints) }.firstOrNull() ==
          ints.let { if (it.contains(1)) it else null }
      }
    }

    with(intFold) {

      "Folding a list of ints" {
        forAll(Arb.list(Arb.int())) { ints ->
          fold(Int.monoid(), ints.k()) == ints.sum()
        }
      }

      "Folding a list should yield same result as combineAll" {
        forAll(Arb.list(Arb.int())) { ints ->
          combineAll(Int.monoid(), ints.k()) == ints.sum()
        }
      }

      "Folding and mapping a list of strings" {
        forAll(Arb.list(Arb.int())) { ints ->
          stringFold.run { foldMap(Int.monoid(), ints.map(Int::toString).k(), String::toInt) } == ints.sum()
        }
      }

      "Get all targets" {
        forAll(Arb.list(Arb.int())) { ints ->
          getAll(ints.k()) == ints.k()
        }
      }

      "Get the size of the fold" {
        forAll(Arb.list(Arb.int())) { ints ->
          size(ints.k()) == ints.size
        }
      }

      "Find the first element matching the predicate" {
        forAll(Arb.list(Arb.int(-100, 100))) { ints ->
          find(ints.k()) { it > 10 } == Option.fromNullable(ints.firstOrNull { it > 10 })
        }
      }

      "Checking existence of a target" {
        forAll(Arb.list(Arb.int()), Arb.bool()) { ints, predicate ->
          exists(ints.k()) { predicate } == (predicate && ints.nonEmpty())
        }
      }

      "Check if all targets match the predicate" {
        forAll(Arb.list(Arb.int())) { ints ->
          forall(ints.k()) { it % 2 == 0 } == ints.all { it % 2 == 0 }
        }
      }

      "Check if there is no target" {
        forAll(Arb.list(Arb.int())) { ints ->
          isEmpty(ints.k()) == ints.isEmpty()
        }
      }

      "Check if there is a target" {
        forAll(Arb.list(Arb.int())) { ints ->
          nonEmpty(ints.k()) == ints.isNotEmpty()
        }
      }
    }
  }
}
