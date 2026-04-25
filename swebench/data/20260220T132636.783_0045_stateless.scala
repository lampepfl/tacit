requestExecPermission(Set("bash")) {
  val result = exec("bash", List("-c", "/opt/homebrew/bin/python3.13 -c \"import django; print(django.__version__)\""))
  println(s"Output: ${result.stdout}")
}