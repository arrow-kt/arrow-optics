package arrow.optics

import arrow.core.Either
import arrow.core.Right
import arrow.optics.extensions.each
import arrow.optics.extensions.uncons
import arrow.optics.extensions.either.each.each
import arrow.optics.extensions.index

fun main() {
  listOf(1, 2, 3).uncons()

  val either: Either<String, Int> = Right(1)
  println(either.each().each().lastOption(either))
  println(Either.each<String, Int>().each().headOption(either))

  sequenceOf(1, 2, 3)

  Sequence::class.index<Int>().index(1).getOption(sequenceOf(1, 2, 3))
}
