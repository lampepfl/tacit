requestExecPermission(Set("python", "python3")) {
  val result = exec("python", List("-m", "pytest", "tests/migrations/test_autodetector.py::AutodetectorTests::test_rename_field_foreign_key_to_field", "-xvs"), 
    workingDir = Option("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo"),
    timeoutMs = 120000)
  println(s"Exit code: ${result.exitCode}")
  println(s"Stdout: ${result.stdout}")
  println(s"Stderr: ${result.stderr}")
}