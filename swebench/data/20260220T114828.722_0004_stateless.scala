requestExecPermission(Set("ls", "find", "grep")) { (pp: ProcessPermission) =>
  val result = exec("ls", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11019/repo"))
  println(result.stdout)
}