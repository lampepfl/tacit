requestExecPermission(Set("ls", "find", "grep")) { (pp: ProcessPermission) =>
  exec("ls", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo"))
}
