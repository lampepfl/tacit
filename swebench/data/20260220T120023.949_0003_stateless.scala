
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11630/repo") { 
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11630/repo/django/db/models/options.py", "database")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}
