
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11630/repo") { 
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11630/repo/django/db", "db_for_model", "*.py")
  matches.take(20).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}
