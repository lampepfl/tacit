requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14238/repo") {
  val result = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14238/repo/django/db/models/fields/__init__.py", "def __subclasscheck__")
  result.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}