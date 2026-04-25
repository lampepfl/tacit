requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  // Look for existing field check tests
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/tests", "fields.E330")
  matches.take(10).foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}
