requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12856/repo") {
  val hits = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12856/repo", "E012", "*.py")
  hits.foreach(h => println(s"${h.file}:${h.lineNumber}: ${h.line}"))
}
