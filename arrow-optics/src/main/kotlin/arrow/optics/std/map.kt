package arrow.optics

import arrow.core.k
import kotlin.reflect.KClass

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
fun <K> KClass<Map<K, Unit>>.toSet(): Iso<Map<K, Unit>, Set<K>> = Iso(
  get = { it.keys.k() },
  reverseGet = { keys -> keys.map { it to Unit }.toMap().k() }
)
