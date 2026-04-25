requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  // Look for where as_sql is called on expressions
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql/compiler.py", "def as_sql")
  matches.take(10).foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}