requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo") { 
requestExecPermission(Set("git")) {
  val gitDiff = exec("git", List("diff", "django/db/models/fields/__init__.py"))
  println(s"Exit: ${gitDiff.exitCode}")
  println(s"stdout: ${gitDiff.stdout}")
  println(s"stderr: ${gitDiff.stderr}")
}
}