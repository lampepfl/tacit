requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12856/repo") {
  val hits = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12856/repo/tests/invalid_models_tests/test_models.py", "unique_together")
  hits.foreach(h => println(s"${h.lineNumber}: ${h.line}"))
}