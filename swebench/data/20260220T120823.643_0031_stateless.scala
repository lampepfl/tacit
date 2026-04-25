requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo") { 
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo/tests/queries/tests.py", "group_by")
  matches.take(10).foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}