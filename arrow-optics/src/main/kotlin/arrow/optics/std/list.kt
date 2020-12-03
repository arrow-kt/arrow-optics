package arrow.optics

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.ListK
import arrow.core.NonEmptyList
import kotlin.reflect.KClass

/**
 * [Optional] to safely operate on the head of a list
 */
@Deprecated("Use List instead of ListK", ReplaceWith("List::class.head()"))
fun <A> ListK.Companion.head(): Optional<List<A>, A> = Optional(
  getOption = { Option.fromNullable(it.firstOrNull()) },
  set = { list, newHead -> list.mapIndexed { index, value -> if (index == 0) newHead else value } }
)

/**
 * [Optional] to safely operate on the head of a list
 */
fun <A> KClass<List<*>>.head(): Optional<List<A>, A> = Optional(
  getOption = { Option.fromNullable(it.firstOrNull()) },
  set = { list, newHead -> list.mapIndexed { index, value -> if (index == 0) newHead else value } }
)

/**
 * [Optional] to safely operate on the tail of a list
 */
@Deprecated("Use List instead of ListK", ReplaceWith("List::class.tail()"))
fun <A> ListK.Companion.tail(): Optional<List<A>, List<A>> = Optional(
  getOption = { if (it.isEmpty()) None else Some(it.drop(1)) },
  set = { list, newTail ->
    list.firstOrNull()?.let {
      listOf(it) + newTail
    } ?: emptyList()
  }
)

/**
 * [Optional] to safely operate on the tail of a list
 */
fun <A> KClass<List<*>>.tail(): Optional<List<A>, List<A>> = Optional(
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
@Deprecated("Use List instead of ListK", ReplaceWith("List::class.toPOptionNel()"))
fun <A, B> ListK.Companion.toPOptionNel(): PIso<List<A>, List<B>, Option<NonEmptyList<A>>, Option<NonEmptyList<B>>> = PIso(
  get = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) },
  reverseGet = { optNel -> optNel.fold({ emptyList() }, NonEmptyList<B>::all) }
)

/**
 * [PIso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A, B> KClass<List<*>>.toPOptionNel(): PIso<List<A>, List<B>, Option<NonEmptyList<A>>, Option<NonEmptyList<B>>> = PIso(
  get = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) },
  reverseGet = { optNel -> optNel.fold({ emptyList() }, NonEmptyList<B>::all) }
)

/**
 * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
@Deprecated("Use List instead of ListK", ReplaceWith("List::class.toOptionNel()"))
fun <A> ListK.Companion.toOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> = toPOptionNel()

/**
 * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A> KClass<List<*>>.toOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> = toPOptionNel()
