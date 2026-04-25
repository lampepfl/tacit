
// Let's check system properties for the classified root configuration
requestExecPermission(Set("java")) {
  val result = exec("java", List(
    "-XshowSettings:all", "-version"
  ))
  println(result.stderr.take(2000))
}
