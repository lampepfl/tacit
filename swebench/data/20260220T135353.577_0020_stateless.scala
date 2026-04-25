requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  // Look for where Q is resolved/compiled
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/query_utils.py", "resolve_expression")
  matches.take(20).foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}