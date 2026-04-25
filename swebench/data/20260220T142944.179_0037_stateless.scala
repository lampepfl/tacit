requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  // Check if there are any existing tests for ordering with inherited models
  val hits = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/tests", "ordering.*inherit", "*.py")
  hits.take(10).foreach(h => println(s"${h.file}:${h.lineNumber}: ${h.line}"))
}