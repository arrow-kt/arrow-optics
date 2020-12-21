package arrow.optics.extensions

import arrow.Kind
import arrow.core.MapInstances
import arrow.core.Option
import arrow.core.Predicate
import arrow.core.extensions.map.traverse.traverse
import arrow.core.fix
import arrow.core.getOption
import arrow.core.k
import arrow.core.left
import arrow.core.right
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

@Deprecated("Obtain instance through Map class", ReplaceWith("Map::class.at<K, V>()"))
fun <K, V> MapInstances.at(): At<Map<K, V>, K, Option<V>> = Map::class.at()

fun <K, V> KClass<Map<*, *>>.at(): At<Map<K, V>, K, Option<V>> = mapAt()

/**
 * [At] instance definition for [Map].
 */
fun <K, V> mapAt(): At<Map<K, V>, K, Option<V>> = At { i ->
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

@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.traversal<K, V>()"))
fun <K, V> MapInstances.traversal(): Traversal<Map<K, V>, V> = Map::class.traversal()

fun <K, V> KClass<Map<*, *>>.traversal(): Traversal<Map<K, V>, V> = MapTraversal()

/**
 * [Traversal] for [Map] that focuses in each [V] of the source [Map].
 */
interface MapTraversal<K, V> : Traversal<Map<K, V>, V> {
  override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> =
    FA.run {
      s.traverse(FA, f).map { it.fix() }
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

@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.each<K, V>()"))
fun <K, V> MapInstances.each(): Each<Map<K, V>, V> = Map::class.each()

fun <K, V> KClass<Map<*, *>>.each(): Each<Map<K, V>, V> = mapEach()

/**
 * [Each] instance definition for [Map].
 */
fun <K, V> mapEach(): Each<Map<K, V>, V> = Each { Map::class.traversal() }

@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.filterIndex<K, V>()"))
fun <K, V> MapInstances.filterIndex(): FilterIndex<Map<K, V>, K, V> = Map::class.filterIndex()

fun <K, V> KClass<Map<*, *>>.filterIndex(): FilterIndex<Map<K, V>, K, V> = filterMapIndex()

/**
 * [FilterIndex] instance definition for [Map].
 */
fun <K, V> filterMapIndex(): FilterIndex<Map<K, V>, K, V> = FilterIndex { p ->
  object : Traversal<Map<K, V>, V> {
    override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
      s.toList().k().traverse(FA) { (k, v) ->
        (if (p(k)) f(v) else just(v)).map {
          k to it
        }
      }.map { it.toMap() }
    }
  }
}

fun <K, V> KClass<Map<*, *>>.filter(p: Predicate<K>): Traversal<Map<K, V>, V> = Map::class.filterIndex<K, V>().filter(p)

@Deprecated("Instance should be obtained through Map class", ReplaceWith("Map::class.index<K, V>()"))
fun <K, V> MapInstances.index(): Index<Map<K, V>, K, V> = Map::class.index()

fun <K, V> KClass<Map<*, *>>.index(): Index<Map<K, V>, K, V> = mapIndex()

/**
 * [Index] instance definition for [Map].
 */
fun <K, V> mapIndex(): Index<Map<K, V>, K, V> = Index { i ->
  POptional(
    getOrModify = { it[i]?.right() ?: it.left() },
    set = { m, v -> m.mapValues { (k, vv) -> if (k == i) v else vv } }
  )
}

fun <K, V> KClass<Map<*, *>>.index(i: K): Optional<Map<K, V>, V> = Map::class.index<K, V>().index(i)
