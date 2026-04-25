requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15498/repo") {
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15498/repo/django/utils/cache.py", "def parse_http_date_safe")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}