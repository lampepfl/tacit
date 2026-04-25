
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14997/repo") {
  requestExecPermission(Set("grep")) {
    exec("grep", List("-r", "UniqueConstraint", "tests/schema/tests.py")).exitCode
  }
}
