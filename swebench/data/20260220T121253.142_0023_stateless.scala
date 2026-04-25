requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo") {
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo/django/db/migrations/autodetector.py", "AlterField")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}