
// Let's look at the RealFileSystem source to understand the isClassified check
requestExecPermission(Set("grep", "find", "cat")) {
  val r = exec("grep", List("-rn", "isClassified\\|requireClassified\\|classified", 
    "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_uh3qu42y"), 
    timeoutMs = 10000)
  println(r.stdout.take(3000))
  println("STDERR: " + r.stderr.take(500))
}
