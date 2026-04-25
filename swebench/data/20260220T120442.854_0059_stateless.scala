requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  requestExecPermission(Set("python", "python3")) {
    // Just syntax check
    val result = exec("python", List("-m", "py_compile", "django/urls/resolvers.py"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo"), timeoutMs = 30000)
    println(s"Exit: ${result.exitCode}")
    println(s"Output: ${result.stdout}")
    println(s"Error: ${result.stderr}")
  }
}