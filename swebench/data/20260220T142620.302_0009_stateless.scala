requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo/django/db/models/fields/__init__.py")
  val content = f.read()
  val start = content.indexOf("get_%s_display")
  val end = content.indexOf("contribute_to_class", start)
  println(content.substring(Math.max(0, start - 300), Math.min(content.length, end)))
}