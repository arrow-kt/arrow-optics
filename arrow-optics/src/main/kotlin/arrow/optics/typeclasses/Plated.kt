package arrow.optics.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.instances.monad
import arrow.instances.sequencek.monoid.monoid
import arrow.optics.*
import arrow.typeclasses.Monad

/**
 * ank_macro_hierarchy(arrow.optics.typeclasses.Plated)
 *
 * [Plated] defines a [Traversal] between a [A] and its immediate children of type [A].
 * It's used to [rewrite] or [transform] a structure internals (children).
 *
 *
 *
 * @param [S] source of [Prism] and init of [Prism] target.
 * @param [A] last of [Prism] focus, [A] is supposed to be unique for a given [S].
 */
interface Plated<A> {

  /**
   * [Traversal] of immediate self-similar children
   */
  fun plate(): Traversal<A, A>

  /**
   * get the immediate self-similar children of a target
   */
  val A.children: List<A>
    get() = plate().getAll(this)

  /**
   * get all transitive self-similar elements of a target, including itself
   */
  val A.universe: SequenceK<A>
    get() {
      val fold = plate().asFold()
      fun go(a: A): SequenceK<A> =
        SequenceK(sequenceOf(a) + fold.foldMap(SequenceK.monoid(), a) { go(it) })
      return go(this)
    }

  /**
   * rewrite a target by applying a rule as often as possible until it reaches
   * a fixpoint (this is an infinite loop if there is no fixpoint)
   */
  fun A.rewrite(f: (A) -> Option<A>): A =
    rewriteOf(plate().asSetter(), f)

  /**
   * rewrite a target by applying a rule within a [Setter], as often as
   * possible until it reaches a fixpoint (this is an infinite loop if there is
   * no fixpoint)
   */
  fun A.rewriteOf(setter: Setter<A, A>, f: (A) -> Option<A>): A {
    tailrec fun go(b: A): A {
      val c = b.transformOf(setter, ::go)
      val result = f(c)

      return when (result) {
        is None -> c
        is Some -> go(result.t)
      }
    }

    return go(this)
  }

  /**
   * transform every element
   */
  fun A.transform(f: (A) -> A): A =
    transformOf(plate().asSetter(), f)

  /**
   * transform every element by applying a [Setter]
   */
  fun A.transformOf(setter: Setter<A, A>, f: (A) -> A): A =
    setter.modify(this) { b ->
      f(b).transformOf(setter, f)
    }

  /**
   * Transforming counting changes
   */
  fun A.transformCounting(f: (A) -> Option<A>): Tuple2<Int, A> =
    transformM(StateApi.monad()) { a ->
      f(a).map { c -> State { i: Int -> Tuple2(i + 1, c) } }
        .getOrElse { StateApi.just(a) }
    }.fix().run(0)

  /**
   * Transforming every element using monadic transformation
   */
  fun <F> A.transformM(MF: Monad<F>, f: (A) -> Kind<F, A>): Kind<F, A> = MF.run {
    val traversal = plate()
    fun go(c: A): Kind<F, A> =
      traversal.modifyF(MF, c) { a -> f(a).flatMap(::go) }

    return go(this@transformM)
  }

}
