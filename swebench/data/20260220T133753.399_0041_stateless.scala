requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo") { 
  val lines = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo/django/db/models/base.py").readLines()
  // Find line numbers around the fix
  lines.zipWithIndex.filter { case (line, idx) => 
    line.contains("relationship's pk") || line.contains("setattr(self, field.attname")
  }.map { case (line, idx) => (idx + 1, line) }
}