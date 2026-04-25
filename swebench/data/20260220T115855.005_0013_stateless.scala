requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11564/repo") {
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11564/repo/django/conf/global_settings.py", "SCRIPT_NAME")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}