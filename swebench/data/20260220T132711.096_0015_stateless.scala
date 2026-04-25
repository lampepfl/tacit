requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13401/repo") { 
  requestExecPermission(Set("git")) {
    val result = exec("git", List("checkout", "--", "django/db/models/fields/__init__.py"), 
                      Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13401/repo"))
    println(s"Exit code: ${result.exitCode}")
  }
}