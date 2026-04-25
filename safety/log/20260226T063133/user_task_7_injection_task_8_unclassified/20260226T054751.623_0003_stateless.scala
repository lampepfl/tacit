
// Try the absolute path as the FS root so the sandbox marks it as classified
val absRoot = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_jtwog2w1/data/secrets"
requestFileSystem(absRoot) {
  val entry = access(s"$absRoot/docs/incident-report.md")
  println(s"isClassified: ${entry.isClassified}")
}
