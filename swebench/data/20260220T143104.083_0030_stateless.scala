requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo") {
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo/django/db/models/options.py", "_forward_fields_map")
  println(s"Found ${matches.size} matches")
  matches.take(10).foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}