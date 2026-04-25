requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  // Find where pk__in=[] raises EmptyResultSet
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models", "EmptyResultSet")
  matches.take(30).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}