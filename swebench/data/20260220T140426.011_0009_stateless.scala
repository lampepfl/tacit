requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo") {
  given fs: FileSystem = summon[FileSystem]
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo/django/db/backends/base/schema.py", "def _constraint_names")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}