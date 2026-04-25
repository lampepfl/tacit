
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  requestExecPermission(Set("git")) {
    exec("git", List("checkout", "--", "django/db/migrations/autodetector.py"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo"))
  }
}
