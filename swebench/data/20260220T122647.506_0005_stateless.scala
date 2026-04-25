requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") {
  // Search for metaclass handling of choices
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo/django/db/models/base.py", "contribute_to_class")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}