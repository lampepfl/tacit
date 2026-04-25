requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14672/repo") { 
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14672/repo/django/db/models/fields/reverse_related.py", "self.through_fields")
  matches.foreach(m => println(s"Line ${m.lineNumber}: ${m.line}"))
}
