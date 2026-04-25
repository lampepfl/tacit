requestExecPermission(Set("bash")) {
  // Try compiling the Python file to check for syntax errors
  val result = exec("bash", List("-c", "/Users/tacit/Work/SafeExecMCP/bench/.venv/bin/python -m py_compile /Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo/django/contrib/admin/options.py"))
  println(s"Exit code: ${result.exitCode}")
  if (result.stderr.nonEmpty) {
    println(s"Errors: ${result.stderr}")
  }
}