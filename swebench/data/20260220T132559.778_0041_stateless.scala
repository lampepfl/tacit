requestExecPermission(Set("bash")) {
  val result = exec("bash", List("-c", "which python3.11 python3.12 2>/dev/null || echo \"not found\""))
  println(s"Output: ${result.stdout}")
}