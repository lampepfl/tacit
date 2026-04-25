// Let me try exec with ls to see the directory structure
requestExecPermission(Set("ls")) { (pp: ProcessPermission) => 
  exec("ls", List("-la", "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo"))
}
