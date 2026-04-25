val g: ProcessPermission => Unit = (pp => {
  val result = exec("ls", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo"))
  println(result.stdout)
})
requestExecPermission(Set("ls", "find", "grep"))(g)