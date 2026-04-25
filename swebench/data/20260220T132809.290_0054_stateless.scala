requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo") {
  // Verify format_lazy is imported
  val matches = grep("django/contrib/admin/options.py", "from django.utils.text import")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}