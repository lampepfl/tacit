val cmds: Set[String] = Set("ls")
requestExecPermission(cmds) { (pp: ProcessPermission) => 
  val r = exec("ls", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12113/repo"))
  val exit = r.exitCode
  val out = r.stdout
  println("Exit: " + exit)
  println("Output: " + out)
}