package arrow.optics.std

//todo this should go in core

inline fun <A, B, R> mapN(a: List<A>, b: List<B>, fn: (A, B) -> R): List<R> =
  a.zip(b, fn)

inline fun <A, B, C, R> mapN(a: List<A>, b: List<B>, c: List<C>, fn: (A, B, C) -> R): List<R> =
  mapN(a, b, c, emptyList<Nothing>()) { a, b, c, _ -> fn(a, b, c) }

inline fun <A, B, C, D, R> mapN(a: List<A>, b: List<B>, c: List<C>, d: List<D>, fn: (A, B, C, D) -> R): List<R> =
  mapN(a, b, c, d, emptyList<Nothing>()) { a, b, c, d, _ -> fn(a, b, c, d) }

inline fun <A, B, C, D, E, R> mapN(
  a: List<A>,
  b: List<B>,
  c: List<C>,
  d: List<D>,
  e: List<E>,
  fn: (A, B, C, D, E) -> R
): List<R> =
  mapN(a, b, c, d, e, emptyList<Nothing>()) { a, b, c, d, e, _ -> fn(a, b, c, d, e) }

inline fun <A, B, C, D, E, F, R> mapN(
  a: List<A>,
  b: List<B>,
  c: List<C>,
  d: List<D>,
  e: List<E>,
  f: List<F>,
  fn: (A, B, C, D, E, F) -> R
): List<R> =
  mapN(a, b, c, d, e, f, emptyList<Nothing>()) { a, b, c, d, e, f, _ -> fn(a, b, c, d, e, f) }

inline fun <A, B, C, D, E, F, G, R> mapN(
  a: List<A>,
  b: List<B>,
  c: List<C>,
  d: List<D>,
  e: List<E>,
  f: List<F>,
  g: List<G>,
  fn: (A, B, C, D, E, F, G) -> R
): List<R> =
  mapN(a, b, c, d, e, f, g, emptyList<Nothing>()) { a, b, c, d, e, f, g, _ -> fn(a, b, c, d, e, f, g) }

inline fun <A, B, C, D, E, F, G, H, R> mapN(
  a: List<A>,
  b: List<B>,
  c: List<C>,
  d: List<D>,
  e: List<E>,
  f: List<F>,
  g: List<G>,
  h: List<H>,
  fn: (A, B, C, D, E, F, G, H) -> R
): List<R> =
  mapN(a, b, c, d, e, f, g, h, emptyList<Nothing>()) { a, b, c, d, e, f, g, h, _ -> fn(a, b, c, d, e, f, g, h) }

inline fun <A, B, C, D, E, F, G, H, I, R> mapN(
  a: List<A>,
  b: List<B>,
  c: List<C>,
  d: List<D>,
  e: List<E>,
  f: List<F>,
  g: List<G>,
  h: List<H>,
  i: List<I>,
  fn: (A, B, C, D, E, F, G, H, I) -> R
): List<R> =
  mapN(a, b, c, d, e, f, g, h, i, emptyList<Nothing>()) { a, b, c, d, e, f, g, h, i, _ -> fn(a, b, c, d, e, f, g, h, i) }

inline fun <A, B, C, D, E, F, G, H, I, R, J> mapN(
  fa: List<A>,
  fb: List<B>,
  fc: List<C>,
  fd: List<D>,
  fe: List<E>,
  ff: List<F>,
  fg: List<G>,
  fh: List<H>,
  fi: List<I>,
  fj: List<J>,
  fn: (A, B, C, D, E, F, G, H, I, J) -> R
): List<R> =
  fa.flatMap { a ->
    fb.flatMap { b ->
      fc.flatMap { c ->
        fd.flatMap { d ->
          fe.flatMap { e ->
            ff.flatMap { f ->
              fg.flatMap { g ->
                fh.flatMap { h ->
                  fi.flatMap { i ->
                    fj.flatMap { j ->
                      listOf(fn(a, b, c, d, e, f, g, h, i, j))
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
