requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  requestExecPermission(Set("python")) {
    exec("python", List("-m", "django", "test", "queries.test_qs_combinators", "--settings=tests.test_sqlite", "-v", "2"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo"), timeoutMs = 120000)
  }
}