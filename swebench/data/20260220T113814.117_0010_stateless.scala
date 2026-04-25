requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-10914/repo") {
  // Search for other tests that may be affected
  val hits = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-10914/repo/tests", "file_permissions_mode", "*.py")
  hits.foreach(h => println(s"${h.file}:${h.lineNumber}: ${h.line}"))
}