requestExecPermission(Set("ls", "echo", "find", "grep")) {
  val result = exec("ls", List("-la", "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo"))
  println(s"Exit: ${result.exitCode}")
  println(s"Stdout: ${result.stdout}")
  println(s"Stderr: ${result.stderr}")
}