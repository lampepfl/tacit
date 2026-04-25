
// Let's try to find the JAR/class files to understand the classified logic
requestExecPermission(Set("find")) {
  val r = exec("find", List("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_465464s8/", "-name", "*.scala", "-o", "-name", "*.jar"))
  println(r.stdout.take(3000))
}
