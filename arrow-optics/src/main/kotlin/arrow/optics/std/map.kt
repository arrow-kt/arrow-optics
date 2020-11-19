package arrow.optics

import kotlin.reflect.KClass

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
fun <K> KClass<Map<*, *>>.toSet(): Iso<Map<K, Unit>, Set<K>> = Iso(
  get = { it.keys },
  reverseGet = { keys -> keys.map { it to Unit }.toMap() }
)
