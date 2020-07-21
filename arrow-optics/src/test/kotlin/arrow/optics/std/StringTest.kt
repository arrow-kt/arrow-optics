package arrow.optics.std

import arrow.optics.test.generators.char
import arrow.optics.toList
import arrow.core.test.UnitSpec
import arrow.optics.test.laws.IsoLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotest.property.Arb

class StringTest : UnitSpec() {

  init {

    testLaws(
      IsoLaws.laws(
        iso = String.toList(),
        aGen = Arb.string(),
        bGen = Arb.list(Arb.char()),
        funcGen = Arb.list(Arb.char()).map { list -> { chars: List<Char> -> list + chars } },
        EQA = Eq.any(),
        EQB = Eq.any(),
        bMonoid = object : Monoid<List<Char>> {
          override fun List<Char>.combine(b: List<Char>): List<Char> = this + b
          override fun empty(): List<Char> = emptyList()
        }
      ))
  }
}
