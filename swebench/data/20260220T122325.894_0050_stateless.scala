requestExecPermission(Set("python", "python3")) {
  val result = exec("python", List("-m", "django", "test", "migrations.test_autodetector.AutodetectorTests.test_rename_field_foreign_key_to_field", "--settings=tests.test_sqlite"), 
    workingDir = Option("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo"),
    timeoutMs = 120000)
  println(s"Exit code: ${result.exitCode}")
  println(s"Stdout: ${result.stdout.take(5000)}")
  println(s"Stderr: ${result.stderr.take(2000)}")
}