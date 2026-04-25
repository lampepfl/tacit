import library.*

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15814/repo") {
  // Use grep to find the exact line
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15814/repo/django/db/models/sql/query.py", "opts = cur_model._meta")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}