package arrow.optics

import arrow.core.MapK
import arrow.core.Option
import arrow.core.SetK
import arrow.core.getOption
import arrow.core.k
import arrow.optics.typeclasses.At
import arrow.typeclasses.Monoid

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
fun <K> MapK.Companion.toSetK(): Iso<MapK<K, Unit>, SetK<K>> = Iso(
  get = { it.keys.k() },
  reverseGet = { keys -> keys.map { it to Unit }.toMap().k() }
)

fun <K, V> At.Companion.map(): At<Map<K, V>, K, Option<V>> =
  At { i ->
    PLens(
      get = { it.getOption(i) },
      set = { map, optV ->
        optV.fold({
          (map - i).k()
        }, {
          (map + (i to it)).k()
        })
      }
    )
  }

fun <K, V> PEvery.Companion.map(): Every<Map<K, V>, V> = object : Every<Map<K, V>, V> {
  override fun <R> foldMap(M: Monoid<R>, s: Map<K, V>, f: (V) -> R): R =
    M.run { s.values.fold(empty()) { acc, v -> acc.combine(f(v)) } }

  override fun modify(s: Map<K, V>, f: (V) -> V): Map<K, V> =
    s.mapValues { (_, v) -> f(v) }
}
