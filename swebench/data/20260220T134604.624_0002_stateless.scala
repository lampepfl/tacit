requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo") {
  val hits = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo/django/utils/decorators.py", "def method_decorator")
  hits.foreach(h => println(s"${h.lineNumber}: ${h.line}"))
}