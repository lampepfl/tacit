requestExecPermission(Set("git")) {
  val result = exec("git", List("checkout", "--", "django/db/migrations/autodetector.py"), workingDir = Option("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo"))
  println(s"Exit code: ${result.exitCode}")
  println(s"Stdout: ${result.stdout}")
  println(s"Stderr: ${result.stderr}")
}