requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo/django/db/models/base.py")
  val content = f.read()
  val start = content.indexOf("def _get_FIELD_display")
  val end = content.indexOf("def ", start + 10)
  println(s"Start: $start, End: $end")
  println(content.substring(start, Math.min(end, start + 800)))
}