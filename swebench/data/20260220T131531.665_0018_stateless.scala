requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  // Check for any tests specifically about limit_choices_to
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/tests/model_forms/tests.py", "limit_choices_to")
  println(s"Found ${matches.length} matches")
  matches.take(10).foreach(m => println(s"${m.lineNumber}: ${m.line.trim}"))
}