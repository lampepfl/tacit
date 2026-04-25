requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12915/repo") {
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12915/repo/tests", "ASGIStaticFilesHandler")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}
