requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  requestExecPermission(Set("python")) {
    exec("PYTHONPATH=/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo python", List("tests/runtests.py", "queries.test_qs_combinators", "-v", "2"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo"), timeoutMs = 180000)
  }
}