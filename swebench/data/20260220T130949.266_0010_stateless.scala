requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo/django/db/models/query.py")
  val lines = f.readLines()
  // Get lines 975-1000 for context
  lines.slice(974, 1000).zipWithIndex.map { case (line, idx) => s"${idx + 975}: $line" }
}