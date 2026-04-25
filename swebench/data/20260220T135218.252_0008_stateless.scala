requestExecPermission(Set("ls", "find")) { pp =>
  val result = exec("ls", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models"))
  println(result.stdout)
}