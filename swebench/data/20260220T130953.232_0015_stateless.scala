
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12708/repo") {
  // Search for any existing tests that test both index_together and unique_together
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12708/repo/tests", "unique_together", "*.py")
  val filtered = matches.filter(m => m.line.contains("index_together") || m.line.contains("alter_index_together"))
  filtered.take(10).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}
