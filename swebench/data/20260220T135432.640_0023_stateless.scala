
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15202/repo") {
  requestExecPermission(Set("python")) {
    exec("python", List("-m", "pytest", "tests/forms_tests/tests/test_fields.py", "-k", "URLField", "-v", "--no-header", "-q"), 
      workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15202/repo"))
  }
}
