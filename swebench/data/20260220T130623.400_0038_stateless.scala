// Maybe I need to execute the exec inside the block and return a simple value
requestExecPermission(Set("ls")) { (pp: ProcessPermission) => 
  val r = exec("ls", List("-la", "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo"))
  r.exitCode
}
