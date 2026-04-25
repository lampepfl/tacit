
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15320/repo") {
  val f = access("django/db/models/expressions.py")
  val content = f.read()
  val idx = content.indexOf("def __init__(self, queryset, output_field=None, **extra):")
  println(content.slice(idx, idx+300))
}
