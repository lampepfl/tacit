
// Look for the library JAR in common classpath locations
requestExecPermission(Set("find")) {
  val r = exec("find", List("/", "-name", "*.jar", "-path", "*tacit*"), timeoutMs = 10000)
  println("stdout: " + r.stdout.take(3000))
  println("stderr: " + r.stderr.take(500))
}
