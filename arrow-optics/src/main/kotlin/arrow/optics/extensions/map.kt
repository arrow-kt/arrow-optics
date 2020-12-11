package arrow.optics.extensions

import arrow.Kind
import arrow.core.MapInstances
import arrow.core.Option
import arrow.core.Predicate
import arrow.core.left
import arrow.core.right
import arrow.core.getOption
import arrow.core.k
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative
import kotlin.reflect.KClass

@Deprecated("Obtain instance through Map class", ReplaceWith("Map::class.at()"))
fun <K, V> MapInstances.at(): At<Map<K, V>, K, Option<V>> = Map::class.at()

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

fun <K, V> KClass<Map<*, *>>.at(i: K): Lens<Map<K, V>, Option<V>> = Map::class.at<K, V>().at(i)

@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.traversal()"))
fun <K, V> MapInstances.traversal(): Traversal<Map<K, V>, V> = MapTraversal()

fun <K, V> KClass<Map<*, *>>.traversal(): Traversal<Map<K, V>, V> = MapTraversal()

/**
 * [Traversal] for [Map] that focuses in each [V] of the source [Map].
 */
interface MapTraversal<K, V> : Traversal<Map<K, V>, V> {
  override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
    s.k().traverse(FA, f)
  }

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke(): MapTraversal<K, V> = object : MapTraversal<K, V> {}
  }
}

@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.each()"))
fun <K, V> MapInstances.each(): Each<Map<K, V>, V> = Map::class.each()

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
  override fun filter(p: Predicate<K>) = object : Traversal<Map<K, V>, V> {
    override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
      s.toList().k().traverse(FA) { (k, v) ->
        (if (p(k)) f(v) else just(v)).map {
          k to it
        }
      }.map { it.toMap() }
    }
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

fun <K, V> KClass<Map<*, *>>.filter(p: Predicate<K>): Traversal<Map<K, V>, V> = Map::class.filterIndex<K, V>().filter(p)

@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.index()"))
fun <K, V> MapInstances.index(): Index<Map<K, V>, K, V> = Map::class.index()

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

fun <K, V> KClass<Map<*, *>>.index(i: K): Optional<Map<K, V>, V> = Map::class.index<K, V>().index(i)
