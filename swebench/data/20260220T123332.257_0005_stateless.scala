
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12497/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12497/repo/django/db/models/fields/related.py")
  val content = f.read()
  val idx = content.indexOf("recursive relationship")
  val start = Math.max(0, idx - 300)
  val end = Math.min(content.length, idx + 500)
  content.substring(start, end)
}
