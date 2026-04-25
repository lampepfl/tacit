requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo") {
  requestExecPermission(Set("python")) {
    val result = exec("python", List("test_fix.py"), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo"), 30000)
    println(s"Exit code: ${result.exitCode}")
    println(s"Stdout: ${result.stdout}")
    println(s"Stderr: ${result.stderr}")
  }
}