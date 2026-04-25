
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15320/repo") {
  requestExecPermission(Set("git")) {
    exec("git", List("checkout", "--", "django/db/models/expressions.py"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15320/repo"))
  }
}
