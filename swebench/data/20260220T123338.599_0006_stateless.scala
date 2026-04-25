
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12497/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12497/repo/django/db/models/fields/related.py")
  val content = f.read()
  val lines = content.split("\n")
  lines.zipWithIndex.filter((line, idx) => line.contains("recursive relationship")).foreach((line, idx) => println(s"${idx+1}: $line"))
}
