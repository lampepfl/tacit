
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12184/repo") {
  // Look for tests related to the resolvers
  grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12184/repo/tests", "def test_.*match", "*.py")
}
