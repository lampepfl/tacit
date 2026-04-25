requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  // Restore again first
  requestExecPermission(Set("git")) {
    exec("git", List("checkout", "--", "django/db/migrations/serializer.py"), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo"))
  }
}