requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo") { 
requestExecPermission(Set("git")) {
  val result = exec("git", List("checkout", "--", "django/db/models/fields/__init__.py"))
  println(s"Exit: ${result.exitCode}")
}
}