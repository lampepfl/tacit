
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14997/repo") {
  requestExecPermission(Set("python")) {
    val result = exec("python", List("tests/runtests.py", "schema.tests.SchemaTests.test_unique", "-v", "2"))
    println(s"Exit: ${result.exitCode}")
    println(s"Stdout: ${result.stdout.take(3000)}")
    println(s"Stderr: ${result.stderr.take(500)}")
  }
}
