requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  requestExecPermission(Set("python")) {
    exec("python", List("-m", "pytest", "tests/queries/test_qs_combinators.py", "-v", "-x", "--tb=short"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo"), timeoutMs = 120000)
  }
}