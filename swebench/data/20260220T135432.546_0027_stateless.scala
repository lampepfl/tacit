requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models", "EmptyResultSet", "*.py")
  matches.take(30).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}