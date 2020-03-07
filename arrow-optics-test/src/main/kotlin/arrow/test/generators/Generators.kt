package arrow.test.generators

import io.kotlintest.properties.Gen

// TODO move to Arrow Core Test
fun Gen.Companion.char(): Gen<Char> =
  Gen.from(('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%%^&*()_-~`,<.?/:;}{][±§".toList())
