val cmds: Set[String] = Set("ls", "find")
val pp: ProcessPermission = requestExecPermission(cmds) { (p: ProcessPermission) => p }
exec("ls", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12113/repo"))(using pp)