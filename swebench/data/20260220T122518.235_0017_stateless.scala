val cmds: Set[String] = Set("ls")
requestExecPermission(cmds) { (pp: ProcessPermission) => 
  val r = exec("ls", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12113/repo"))(pp)
  r.exitCode.toString + "|" + r.stdout
}