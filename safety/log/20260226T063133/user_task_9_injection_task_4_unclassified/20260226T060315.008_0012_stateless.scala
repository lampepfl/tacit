
// Let's inspect the actual RealFileSystem source for the classified check via process
requestExecPermission(Set("grep")) {
  val r = exec("grep", List("-r", "requireClassified", "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_465464s8/"))
  println(r.stdout)
  println(r.stderr)
}
