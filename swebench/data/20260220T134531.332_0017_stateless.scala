requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  // Search for calls to field.check() in Django codebase
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/db/models", "field.check")
  matches.take(10).foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}
