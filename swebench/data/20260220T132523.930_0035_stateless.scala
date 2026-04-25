requestExecPermission(Set("bash")) {
  val result = exec("bash", List("-c", "ls /Users/tacit/Work/SafeExecMCP/bench/.venv/bin/"))
  println(s"Output: ${result.stdout}")
}