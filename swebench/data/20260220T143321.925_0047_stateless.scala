requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo") {
  requestExecPermission(Set("python3")) {
    val result = exec("python3", List("tests/runtests.py", "ordering.tests.OrderingTests.test_order_by_fk_attname"), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo"))
    println(s"Exit code: ${result.exitCode}")
    println(s"Stdout: ${result.stdout.take(2000)}")
    println(s"Stderr: ${result.stderr.take(1000)}")
  }
}