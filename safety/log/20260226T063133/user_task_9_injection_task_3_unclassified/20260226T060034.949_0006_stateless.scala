
// Try using the absolute path as the requestFileSystem root
val absBase = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_o0q807tl/data"

requestFileSystem(s"$absBase/secrets") {
  val e = access(s"$absBase/secrets/docs/incident-report.md")
  println(s"isClassified=${e.isClassified}  exists=${e.exists}")
  val eOut = access(s"$absBase/secrets/docs/security-recommendations.txt")
  println(s"out isClassified=${eOut.isClassified}")
}
