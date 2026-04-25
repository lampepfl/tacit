requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  // Look for existing tests related to limit_choices_to and duplicates
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/tests/model_forms/tests.py", "limit_choices_to.*duplicate")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line.trim}"))
}