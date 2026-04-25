// The files report isClassified=false when rooted at "secrets".
// Try with the absolute path prefix that appears in the walk output.
val absRoot = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_syufbkax/data/secrets"

requestFileSystem(absRoot) {
  val entries = access(absRoot).walk()
  entries.foreach { e =>
    println(s"${e.name} | classified=${e.isClassified}")
  }
}
