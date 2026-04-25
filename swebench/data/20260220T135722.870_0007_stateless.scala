
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15320/repo") {
  val matches = grep("django/db/models/sql/query.py", "subquery")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}
