val cmds: Set[String] = Set("ls", "find")
val lambda = requestExecPermission(cmds) { (pp: ProcessPermission) => 
  exec("ls", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12113/repo"))(using pp)
}
val io = summon[IOCapability]
println(io)
lambda