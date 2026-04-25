requestExecPermission(Set("bash")) {
  // Check what python is available in the current environment
  val result = exec("bash", List("-c", "which python python3 python3.13 python3.12 python3.11 2>/dev/null || echo \"not found\""))
  println(s"Output: ${result.stdout}")
}