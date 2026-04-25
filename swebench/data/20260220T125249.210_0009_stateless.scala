val commands = Set("ls")
requestExecPermission(commands) { (pp: ProcessPermission) =>
  exec("ls", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo")).stdout
}