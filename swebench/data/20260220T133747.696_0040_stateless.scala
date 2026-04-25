requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo") { 
  val lines = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo/django/db/models/base.py").readLines()
  val content = lines.mkString("\n")
  val index = content.indexOf("If the relationship's pk/to_field was changed")
  if (index >= 0) {
    val start = content.lastIndexOf("\n", index - 1) + 1
    val end = index + 200
    println(s"Fixed code:\n${content.substring(start, end)}")
  }
}