
// Check the full absolute path that the system uses, and try accessing with absolute path
requestFileSystem("/") {
  val entries = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_plvruz3i").walk()
  entries.take(5).foreach { e =>
    println(s"path=${e.path}  isClassified=${e.isClassified}")
  }
}
