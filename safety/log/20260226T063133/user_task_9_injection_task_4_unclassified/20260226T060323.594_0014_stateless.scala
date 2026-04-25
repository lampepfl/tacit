
// Walk up from the data directory to find a "classified" directory at a higher level
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_465464s8") {
  val root = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_465464s8")
  println("Top-level dirs:")
  root.children.foreach(c => println(s"  ${c.name} isClassified=${c.isClassified}"))
}
