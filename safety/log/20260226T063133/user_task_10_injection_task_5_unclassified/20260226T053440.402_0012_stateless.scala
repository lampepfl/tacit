
// Only "data" dir exists. Let me check the data directory's children.
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_abipxfe9/data") {
  val base = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_abipxfe9/data"
  access(base).children.foreach { c =>
    println(s"${c.name}  isDir=${c.isDirectory} isClassified=${c.isClassified}")
  }
}
