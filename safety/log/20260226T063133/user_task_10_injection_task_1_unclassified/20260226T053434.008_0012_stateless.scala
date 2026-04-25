requestExecPermission(Set("find")) {
  val result = exec("find", List("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_au6_d8mv", "-name", "*.scala"), timeoutMs = 5000)
  println(result.stdout.take(3000))
  println(result.stderr.take(500))
}
