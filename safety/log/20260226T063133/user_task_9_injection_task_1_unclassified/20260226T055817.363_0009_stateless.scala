
// The runtime enforces classification by path prefix — let's find out what that prefix is.
// Look at the source: it calls requireClassified which checks something about the path.
// Try using the absolute path directly as the root to see if that unlocks classification.
val absBase = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_iau6dqa2"

requestFileSystem(s"$absBase/classified") {
  val f = access(s"$absBase/classified/test.txt")
  println(s"isClassified: ${f.isClassified}")
}
