package arrow.optics.extensions

import arrow.core.MapK
import arrow.core.Option
import arrow.core.fix
import arrow.core.getOption
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.extension
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index

/**
 * [At] instance definition for [MapK].
 */
@extension
interface MapKAt<K, V> : At<MapK<K, V>, K, Option<V>> {
  override fun at(i: K): Lens<MapK<K, V>, Option<V>> = PLens(
    get = { it.fix().getOption(i) },
    set = { map, optV ->
      optV.fold({
        (map - i).k()
      }, {
        (map + (i to it)).k()
      })
    }
  )
}

/**
 * [Traversal] for [MapK] that has focus in each [V].
 *
 * @receiver [MapK.Companion] to make it statically available.
 * @return [Traversal] with source [MapK] and focus every [V] of the source.
 */
fun <K, V> MapK.Companion.traversal(): Traversal<MapK<K, V>, V> =
  Traversal { s, f -> s.map(f) }

/**
 * [Each] instance definition for [Map].
 */
@extension
interface MapKEach<K, V> : Each<MapK<K, V>, V> {
  override fun each(): Traversal<MapK<K, V>, V> =
    MapK.traversal()
}

/**
 * [FilterIndex] instance definition for [Map].
 */
@extension
interface MapKFilterIndex<K, V> : FilterIndex<MapK<K, V>, K, V> {
  override fun filter(p: (K) -> Boolean): Traversal<MapK<K, V>, V> =
    Traversal { s, f ->
      s.toList().map { (k, v) ->
        k to (if (p(k)) f(v) else v)
      }.toMap().k()
    }
}

/**
 * [Index] instance definition for [Map].
 */
@extension
interface MapKIndex<K, V> : Index<MapK<K, V>, K, V> {
  override fun index(i: K): Optional<MapK<K, V>, V> = POptional(
    getOrModify = { it[i]?.right() ?: it.left() },
    set = { m, v -> m.mapValues { (k, vv) -> if (k == i) v else vv }.k() }
  )
}
