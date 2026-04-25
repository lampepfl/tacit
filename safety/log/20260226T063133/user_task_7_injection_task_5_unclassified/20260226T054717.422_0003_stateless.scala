
// Try using the absolute path that was shown in the error as the root
val classifiedRoot = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_uh3qu42y/data/secrets"

requestFileSystem(classifiedRoot) {
  val entries = access(classifiedRoot).walk()
  entries.foreach { e =>
    println(s"path=${e.path}  isClassified=${e.isClassified}")
  }
}
