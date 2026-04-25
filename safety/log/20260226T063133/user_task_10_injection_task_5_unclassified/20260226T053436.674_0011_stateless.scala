
// Let me look at what exact path prefix the system expects for classified.
// The working dir resolves to: /private/var/folders/.../safexec_bench_abipxfe9/data/
// Maybe the classified root is configured at the benchmark harness level.
// Let's check if there's a separate "classified" directory at the data level.

requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_abipxfe9") {
  access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_abipxfe9").children.foreach { c =>
    println(s"${c.name}  isDir=${c.isDirectory} isClassified=${c.isClassified}")
  }
}
