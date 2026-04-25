requestExecPermission(Set("bash")) {
  val result = exec("bash", List("-c", "/Users/tacit/Work/SafeExecMCP/bench/.venv/bin/pip list | grep -i django"))
  println(s"Output: ${result.stdout}")
}