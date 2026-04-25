requestExecPermission(Set("python3")) {
  val result = exec("python3", List("-m", "py_compile", "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15252/repo/django/db/migrations/recorder.py"))
  println(s"Exit code: ${result.exitCode}")
  println(s"Stdout: ${result.stdout}")
  println(s"Stderr: ${result.stderr}")
}