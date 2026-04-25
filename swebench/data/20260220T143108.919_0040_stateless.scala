requestExecPermission(Set("git")) {
  val result = exec("git", List("diff", "django/db/migrations/serializer.py"), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo"))
  println(s"Exit code: ${result.exitCode}")
  println(s"stdout: ${result.stdout}")
}