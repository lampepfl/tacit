import library.*

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11049/repo") { (fs: FileSystem) ?=>
  given FileSystem = fs
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11049/repo/django/db/models/fields/__init__.py", "\\[DD\\] \\[HH")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}