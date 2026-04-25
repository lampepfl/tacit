requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  requestExecPermission(Set("git")) {
    val result = exec("git", List("diff", "django/urls/resolvers.py"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo"))
    println(s"Exit: ${result.exitCode}")
    println(s"Output:\n${result.stdout}")
  }
}