requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  // Look for group_by related code that might have similar issues
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql", "get_group_by", "*.py")
  matches.take(10).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}