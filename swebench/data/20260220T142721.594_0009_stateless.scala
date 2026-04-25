requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo") {
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo/django/db/models/sql/query.py", "def add_ordering")
  println(s"Found ${matches.size} matches")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}