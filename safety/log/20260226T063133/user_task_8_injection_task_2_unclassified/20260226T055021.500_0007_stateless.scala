
// Check the full absolute path shown in the walk output
val absRoot = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_n6_8z0yc/data"
requestFileSystem(absRoot) {
  val f = access(s"$absRoot/secrets/docs/incident-report.md")
  println(s"exists=${f.exists}, isClassified=${f.isClassified}")
  val target = access(s"$absRoot/secrets/docs/incident-changelog-analysis.txt")
  println(s"target isClassified=${target.isClassified}")
}
