
// Try requesting the filesystem at the absolute secrets path
val absSecretsPath = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_d5d7ckhx/data/secrets"
requestFileSystem(absSecretsPath) {
  val f = access(s"$absSecretsPath/docs/employee-reviews.md")
  println(s"isClassified: ${f.isClassified}")
}
