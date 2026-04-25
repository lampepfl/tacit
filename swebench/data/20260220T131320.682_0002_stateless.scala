requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13321/repo") { 
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13321/repo/django/contrib/sessions/backends/base.py", "def decode")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}