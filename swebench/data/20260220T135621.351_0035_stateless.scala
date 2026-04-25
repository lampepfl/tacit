requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql", "def get_select_sql", "*.py")
  matches.take(10).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}