requestExecPermission(Set("python3")) {
  val result = exec("python3", List("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/test_inner_class.py"), None, 30000)
  println(s"Exit code: ${result.exitCode}")
  println(s"stdout: ${result.stdout}")
  println(s"stderr: ${result.stderr}")
}