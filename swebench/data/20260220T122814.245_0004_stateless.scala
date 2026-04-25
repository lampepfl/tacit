requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val jsonField = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo/django/db/models/fields/json.py")
  val content = jsonField.read()
  
  // Find prepare_value method
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo/django/db/models/fields/json.py", "def prepare_value")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}
