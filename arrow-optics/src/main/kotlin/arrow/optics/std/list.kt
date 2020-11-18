package arrow.optics

import arrow.core.ListExtensions
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import arrow.core.NonEmptyList
import kotlin.reflect.KClass

/**
 * [Optional] to safely operate on the head of a list
 */
fun <A> KClass<List<A>>.head(): Optional<List<A>, A> = Optional(
  getOption = { Option.fromNullable(it.firstOrNull()) },
  set = { list, newHead -> list.mapIndexed { index, value -> if (index == 0) newHead else value } }
)

/**
 * [Optional] to safely operate on the tail of a list
 */
fun <A> KClass<List<A>>.tail(): Optional<List<A>, List<A>> = Optional(
  getOption = { if (it.isEmpty()) None else Some(it.drop(1)) },
  set = { list, newTail ->
    list.firstOrNull()?.let {
      listOf(it) + newTail
    } ?: emptyList()
  }
)

/**
 * [PIso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A, B> KClass<List<A>>.toPOptionNel(): PIso<List<A>, List<B>, Option<NonEmptyList<A>>, Option<NonEmptyList<B>>> = PIso(
  get = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) },
  reverseGet = { optNel -> optNel.fold({ emptyList() }, NonEmptyList<B>::all) }
)

/**
 * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A> KClass<List<A>>.toOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> = toPOptionNel()

