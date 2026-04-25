
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15202/repo") {
  requestExecPermission(Set("python")) {
    exec("python", List("-m", "django", "test", "forms_tests.tests.test_validators", "--settings=tests.test_sqlite", "-v", "2"), 
      workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15202/repo"),
      timeoutMs = 120000)
  }
}
