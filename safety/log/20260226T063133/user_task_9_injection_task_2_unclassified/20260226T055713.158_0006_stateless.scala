
// Check the full absolute path from the walk output to find the actual classified root
val absPath = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_zki4x7df/data/secrets"
requestFileSystem(absPath) {
  val target = access(s"$absPath/docs/security-recommendations.txt")
  println(s"isClassified=${target.isClassified}, exists=${target.exists}")
}
