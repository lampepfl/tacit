requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo/django/db/models/query.py")
  val lines = f.readLines()
  // Get lines 1080-1100 for annotate method
  lines.slice(1083, 1102).zipWithIndex.map { case (line, idx) => s"${idx + 1084}: $line" }
}