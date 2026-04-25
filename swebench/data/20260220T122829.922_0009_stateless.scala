requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  // Search for JSONField in forms/fields.py
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo/django/forms/fields.py", "class JSONField")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}
