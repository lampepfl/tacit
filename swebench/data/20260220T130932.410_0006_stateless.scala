requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo/django/db/models/query.py")
  val lines = f.readLines()
  // Get lines 1120-1160 for context
  lines.slice(1119, 1160).zipWithIndex.map { case (line, idx) => s"${idx + 1120}: $line" }
}