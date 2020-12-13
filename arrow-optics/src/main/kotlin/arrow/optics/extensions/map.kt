package arrow.optics.extensions

import arrow.Kind
import arrow.core.MapInstances
import arrow.core.Option
import arrow.core.Predicate
import arrow.core.extensions.map.functor.map
import arrow.core.left
import arrow.core.right
import arrow.core.getOption
import arrow.core.k
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import kotlin.reflect.KClass

@Deprecated("Obtain instance through Map class", ReplaceWith("Map::class.at()"))
fun <K, V> MapInstances.at(): At<Map<K, V>, K, Option<V>> = mapAt()

fun <K, V> KClass<Map<*, *>>.at(): At<Map<K, V>, K, Option<V>> = mapAt()

/**
 * [At] instance definition for [Map].
 */
inline fun <K, V> mapAt(): At<Map<K, V>, K, Option<V>> = At { i ->
  PLens(
    get = { it.getOption(i) },
    set = { map, optV ->
      optV.fold(
        { (map - i) },
        { (map + (i to it)) }
      )
    }
  )
}

@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.traversal()"))
fun <K, V> MapInstances.traversal(): Traversal<Map<K, V>, V> =
  Map::class.traversal()

fun <K, V> KClass<Map<*, *>>.traversal(): Traversal<Map<K, V>, V> =
  Traversal { s, f -> s.map(f) }


@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.each()"))
fun <K, V> MapInstances.each(): Each<Map<K, V>, V> = mapEach()

fun <K, V> KClass<Map<*, *>>.each(): Each<Map<K, V>, V> = mapEach()

/**
 * [Each] instance definition for [Map].
 */
inline fun <K, V> mapEach(): Each<Map<K, V>, V> = Each { Map::class.traversal() }

@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.filterIndex()"))
fun <K, V> MapInstances.filterIndex(): FilterIndex<Map<K, V>, K, V> = FilterMapIndex()

fun <K, V> KClass<Map<*, *>>.filterIndex(): FilterIndex<Map<K, V>, K, V> = FilterMapIndex()

@Deprecated("Use the type with the correct capitalization", ReplaceWith("FilterMapIndex<K, V>"))
typealias filterMapIndex<K, V> = FilterMapIndex<K, V>

/**
 * [FilterIndex] instance definition for [Map].
 */
interface FilterMapIndex<K, V> : FilterIndex<Map<K, V>, K, V> {
  override fun filter(p: Predicate<K>) =
    Traversal<Map<K, V>, V> { s, f ->
      s.toList().map { (k, v) ->
        k to (if (p(k)) f(v) else v)
      }.toMap()
    }

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke() = object : FilterMapIndex<K, V> {}
  }
}

@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.index()"))
fun <K, V> MapInstances.index(): Index<Map<K, V>, K, V> = mapIndex()

fun <K, V> KClass<Map<*, *>>.index(): Index<Map<K, V>, K, V> = mapIndex()

/**
 * [Index] instance definition for [Map].
 */
inline fun <K, V> mapIndex(): Index<Map<K, V>, K, V> = Index { i ->
  POptional(
    getOrModify = { it[i]?.right() ?: it.left() },
    set = { m, v -> m.mapValues { (k, vv) -> if (k == i) v else vv } }
  )
}
