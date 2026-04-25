requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo") {
  // Search for id_for_label in the Widget class
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo/django/forms/widgets.py", "def id_for_label")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}