val cmds: Set[String] = Set("ls", "find")
requestExecPermission(cmds) { (pp: ProcessPermission) => 
  val result = exec("ls", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12113/repo"))(using pp)
  println(s"Exit: ${result.exitCode}")
  println(s"Stdout: ${result.stdout}")
}