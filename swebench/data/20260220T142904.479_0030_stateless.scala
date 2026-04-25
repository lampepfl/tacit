requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  val hits = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/django/db/models/sql", "ORDER_DIR", "*.py")
  hits.foreach(h => println(s"${h.file}:${h.lineNumber}: ${h.line}"))
}