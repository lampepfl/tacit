
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  // Look for code related to ordering in models
  val hits = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/django", "Meta.*ordering", "*.py")
  hits.take(20).foreach(h => println(s"${h.file}:${h.lineNumber}: ${h.line}"))
}
