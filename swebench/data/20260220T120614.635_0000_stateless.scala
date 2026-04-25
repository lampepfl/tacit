requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val matches = grep(".", "parse_http_date")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}