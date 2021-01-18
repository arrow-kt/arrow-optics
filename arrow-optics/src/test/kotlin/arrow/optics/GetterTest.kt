package arrow.optics

import arrow.core.Left
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.extensions.monoid
import arrow.core.k
import arrow.core.test.UnitSpec
import arrow.core.toT
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class GetterTest : UnitSpec() {

  init {

    val userGetter = userIso.asGetter()
    val length = Getter<String, Int> { it.length }
    val upper = Getter<String, String> { it.toUpperCase() }

    with(tokenGetter.asFold()) {

      "asFold should behave as valid Fold: size" {
        forAll(genToken) { token ->
          size(token) == 1
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(genToken) { token ->
          nonEmpty(token)
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(genToken) { token ->
          !isEmpty(token)
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(genToken) { token ->
          getAll(token) == listOf(token.value).k()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(genToken) { token ->
          combineAll(String.monoid(), token) == token.value
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(genToken) { token ->
          fold(String.monoid(), token) == token.value
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(genToken) { token ->
          headOption(token) == Some(token.value)
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(genToken) { token ->
          lastOption(token) == Some(token.value)
        }
      }
    }

    with(tokenGetter) {

      "Getting the target should always yield the exact result" {
        forAll { value: String ->
          get(Token(value)) == value
        }
      }

      "Finding a target using a predicate within a Getter should be wrapped in the correct option result" {
        forAll { value: String, predicate: Boolean ->
          find(Token(value)) { predicate }.fold({ false }, { true }) == predicate
        }
      }

      "Checking existence of a target should always result in the same result as predicate" {
        forAll { value: String, predicate: Boolean ->
          exist(Token(value)) { predicate } == predicate
        }
      }
    }

    "Zipping two lenses should yield a tuple of the targets" {
      forAll { value: String ->
        length.zip(upper).get(value) == value.length toT value.toUpperCase()
      }
    }

    "Joining two getters together with same target should yield same result" {
      val userTokenStringGetter = userGetter compose tokenGetter
      val joinedGetter = tokenGetter.choice(userTokenStringGetter)

      forAll { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        joinedGetter.get(Left(token)) == joinedGetter.get(Right(user))
      }
    }

    "Pairing two disjoint getters should yield a pair of their results" {
      val splitGetter: Getter<Tuple2<Token, User>, Tuple2<String, Token>> = tokenGetter.split(userGetter)
      forAll(genToken, genUser) { token: Token, user: User ->
        splitGetter.get(token toT user) == token.value toT user.token
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenGetter.first<Int>()
      forAll(genToken, Gen.int()) { token: Token, int: Int ->
        first.get(token toT int) == token.value toT int
      }
    }

    "Creating a second pair with a type should result in the value target" {
      val first = tokenGetter.second<Int>()
      forAll(Gen.int(), genToken) { int: Int, token: Token ->
        first.get(int toT token) == int toT token.value
      }
    }
  }
}
