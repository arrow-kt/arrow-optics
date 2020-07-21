package arrow.optics.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.of

fun Arb.Companion.char(): Arb<Char> =
  Arb.of(('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%%^&*()_-~`,<.?/:;}{][±§".toList())
