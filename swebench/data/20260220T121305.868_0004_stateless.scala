
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo") {
  val fs = summon[FileSystem]
  val pattern = "get_.*_display"
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo/django/db/models/fields/__init__.py", pattern)
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}
