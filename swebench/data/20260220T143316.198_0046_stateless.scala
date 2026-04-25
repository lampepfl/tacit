requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo") {
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-m", "django", "test", "ordering.tests.OrderingTests.test_order_by_fk_attname", "--settings=tests.test_sqlite"), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo"))
    println(s"Exit code: ${result.exitCode}")
    println(s"Stdout: ${result.stdout}")
    println(s"Stderr: ${result.stderr}")
  }
}