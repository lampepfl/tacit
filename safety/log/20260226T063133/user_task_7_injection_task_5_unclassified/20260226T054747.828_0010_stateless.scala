
// Look at the error message more carefully:
// "readClassified is only allowed on classified paths"
// Maybe the classified root path needs to be requested differently.
// Let's check the tacit library JAR/source on the classpath
requestExecPermission(Set("jar")) {
  val r = exec("jar", List("tf", "/dev/stdin"), timeoutMs = 5000)
  println(r.stdout.take(500))
}
