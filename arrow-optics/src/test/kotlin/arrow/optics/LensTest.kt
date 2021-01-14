package arrow.optics

import arrow.core.Left
import arrow.core.ListK
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.functor.functor
import arrow.core.k
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.toT
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class LensTest : UnitSpec() {

  init {
    testLaws(
      LensLaws.laws(
        lens = tokenLens,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      ),

      TraversalLaws.laws(
        traversal = tokenLens.asTraversal(),
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      OptionalLaws.laws(
        optional = tokenLens.asOptional(),
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      ),

      SetterLaws.laws(
        setter = tokenLens.asSetter(),
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Token.eq()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.id(),
        aGen = Gen.int(),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Int.monoid()
      )
    )

    "asFold should behave as valid Fold: size" {
      forAll(genToken) { token ->
        tokenLens.asFold().size(token) == 1
      }
    }

    "asFold should behave as valid Fold: nonEmpty" {
      forAll(genToken) { token ->
        tokenLens.asFold().nonEmpty(token)
      }
    }

    "asFold should behave as valid Fold: isEmpty" {
      forAll(genToken) { token ->
        !tokenLens.asFold().isEmpty(token)
      }
    }

    "asFold should behave as valid Fold: getAll" {
      forAll(genToken) { token ->
        tokenLens.asFold().getAll(token) == listOf(token.value).k()
      }
    }

    "asFold should behave as valid Fold: combineAll" {
      forAll(genToken) { token ->
        tokenLens.asFold().combineAll(String.monoid(), token) == token.value
      }
    }

    "asFold should behave as valid Fold: fold" {
      forAll(genToken) { token ->
        tokenLens.asFold().fold(String.monoid(), token) == token.value
      }
    }

    "asFold should behave as valid Fold: headOption" {
      forAll(genToken) { token ->
        tokenLens.asFold().headOption(token) == Some(token.value)
      }
    }

    "asFold should behave as valid Fold: lastOption" {
      forAll(genToken) { token ->
        tokenLens.asFold().lastOption(token) == Some(token.value)
      }
    }

    "asGetter should behave as valid Getter: get" {
      forAll(genToken) { token ->
        tokenLens.asGetter().get(token) == tokenGetter.get(token)
      }
    }

    "asGetter should behave as valid Getter: find" {
      forAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
        tokenLens.asGetter().find(token, p) == tokenGetter.find(token, p)
      }
    }

    "asGetter should behave as valid Getter: exist" {
      forAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
        tokenLens.asGetter().exist(token, p) == tokenGetter.exist(token, p)
      }
    }

    "Lifting a function should yield the same result as not yielding" {
      forAll(genToken, Gen.string()) { token, value ->
        tokenLens.set(token, value) == tokenLens.lift { value }(token)
      }
    }

    "Lifting a function as a functor should yield the same result as not yielding" {
      forAll(genToken, Gen.string()) { token, value ->
        tokenLens.modifyF(Option.functor(), token) { Some(value) } == tokenLens.liftF(Option.functor()) { Some(value) }(
          token
        )
      }
    }

    "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
      forAll { predicate: Boolean ->
        tokenLens.find(Token("any value")) { predicate }.fold({ false }, { true }) == predicate
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      forAll { predicate: Boolean ->
        tokenLens.exist(Token("any value")) { predicate } == predicate
      }
    }

    "Joining two lenses together with same target should yield same result" {
      val userTokenStringLens = userLens compose tokenLens
      val joinedLens = tokenLens choice userTokenStringLens

      forAll { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        joinedLens.get(Left(token)) == joinedLens.get(Right(user))
      }
    }

    "Pairing two disjoint lenses should yield a pair of their results" {
      val spiltLens: Lens<Tuple2<Token, User>, Tuple2<String, Token>> = tokenLens split userLens
      forAll(genToken, genUser) { token: Token, user: User ->
        spiltLens.get(token toT user) == token.value toT user.token
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenLens.first<Int>()
      forAll(genToken, Gen.int()) { token: Token, int: Int ->
        first.get(token toT int) == token.value toT int
      }
    }

    "Creating a second pair with a type should result in the value target" {
      val second = tokenLens.second<Int>()
      forAll(Gen.int(), genToken) { int: Int, token: Token ->
        second.get(int toT token) == int toT token.value
      }
    }
  }
}
