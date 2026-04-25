requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo") { 
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo/django/db/models/sql/query.py", "group_by")
  matches.take(30).foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}