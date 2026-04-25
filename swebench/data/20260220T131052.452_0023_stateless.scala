requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo/django/db/models/query.py")
  val lines = f.readLines()
  // Get lines 1147-1156 for extra method
  lines.slice(1146, 1158).zipWithIndex.map { case (line, idx) => s"${idx + 1147}: $line" }
}