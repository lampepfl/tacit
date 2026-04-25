requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo") {
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo/tests/migrations/test_autodetector.py", "test_rename_field_foreign_key_to_field")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}