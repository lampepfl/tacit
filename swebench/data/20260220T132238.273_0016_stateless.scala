requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo") {
  // Search for format_lazy import in options.py
  val matches = grep("django/contrib/admin/options.py", "format_lazy")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}