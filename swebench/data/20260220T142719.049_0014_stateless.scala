requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  // Search for how parent ordering is propagated
  val hits = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/django/db", "parent.*order", "*.py")
  hits.foreach(h => println(s"${h.file}:${h.lineNumber}: ${h.line}"))
}