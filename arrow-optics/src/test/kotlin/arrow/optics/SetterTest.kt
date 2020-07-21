package arrow.optics

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.core.extensions.option.functor.functor
import arrow.core.getOrElse
import arrow.mtl.State
import arrow.mtl.run
import arrow.optics.mtl.assign_
import arrow.optics.mtl.update_
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.option
import arrow.optics.test.laws.SetterLaws
import arrow.typeclasses.Eq
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll

class SetterTest : UnitSpec() {

  init {

    testLaws(SetterLaws.laws(
      setter = Setter.id(),
      aGen = Arb.int(),
      bGen = Arb.int(),
      funcGen = Arb.functionAToB(Arb.int()),
      EQA = Eq.any()
    ))

    testLaws(SetterLaws.laws(
      setter = tokenSetter,
      aGen = genToken,
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string()),
      EQA = Eq.any()
    ))

    testLaws(SetterLaws.laws(
      setter = Setter.fromFunctor<ForOption, String, String>(Option.functor()),
      aGen = Arb.option(Arb.string()) as Arb<Kind<ForOption, String>>,
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string()),
      EQA = Eq.any()
    ))

    "Joining two lenses together with same target should yield same result" {
      val userTokenStringSetter = userSetter compose tokenSetter
      val joinedSetter = tokenSetter.choice(userTokenStringSetter)
      val oldValue = "oldValue"
      val token = Token(oldValue)
      val user = User(token)

      forAll { value: String ->
        joinedSetter.set(token.left(), value).swap().getOrElse { Token("Wrong value") }.value ==
          joinedSetter.set(user.right(), value).getOrElse { User(Token("Wrong value")) }.token.value
      }
    }

    "Lifting a function should yield the same result as direct modify" {
      forAll(genToken, Arb.string()) { token, value ->
        tokenSetter.modify(token) { value } == tokenSetter.lift { value }(token)
      }
    }

    "update_ f should be as modify f within State and returning Unit" {
      forAll(genToken, Arb.functionAToB<String, String>(Arb.string())) { generatedToken, f ->
        tokenSetter.update_(f).run(generatedToken) ==
          State { token: Token ->
            tokenSetter.modify(token, f) toT Unit
          }.run(generatedToken)
      }
    }

    "assign_ f should be as modify f within State and returning Unit" {
      forAll(genToken, Arb.string()) { generatedToken, string ->
        tokenSetter.assign_(string).run(generatedToken) ==
          State { token: Token ->
            tokenSetter.set(token, string) toT Unit
          }.run(generatedToken)
      }
    }
  }
}
