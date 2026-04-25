requestExecPermission(Set("bash")) {
  val result = exec("bash", List("-c", "/Users/tacit/Work/SafeExecMCP/bench/.venv/bin/python -c \"import django; print(django.__version__)\""))
  println(s"Output: ${result.stdout}")
}