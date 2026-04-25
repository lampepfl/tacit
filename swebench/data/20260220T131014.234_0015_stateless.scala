requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo/django/db/models/query.py")
  val lines = f.readLines()
  // Find _not_support_combined_queries definition
  lines.zipWithIndex.filter { case (line, idx) => line.contains("def _not_support_combined_queries") }.map { case (line, idx) => s"${idx + 1}: $line" }
}