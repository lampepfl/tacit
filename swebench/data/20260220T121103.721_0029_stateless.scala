requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  requestExecPermission(Set("python", "pytest")) {
    val result = exec("python", List("-m", "pytest", "tests/utils_tests/test_http.py", "-v", "-k", "parse_http_date", "--no-header"))
    println(s"stdout: ${result.stdout.take(2000)}")
    println(s"stderr: ${result.stderr.take(500)}")
    println(s"exit: ${result.exitCode}")
  }
}