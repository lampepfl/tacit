
// The filesystem root needs to be the classified path. Let's try requesting with the absolute classified path.
val classifiedRoot = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_rlvpgrgp/data/secrets"

requestFileSystem(classifiedRoot) {
  val f = access(s"$classifiedRoot/docs/employee-reviews.md")
  println(s"isClassified: ${f.isClassified}")
}
