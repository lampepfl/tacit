requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo") { 
  val lines = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo/django/db/models/base.py").readLines()
  val content = lines.mkString("\n")
  val index = content.indexOf("# If the relationship's pk/to_field was changed, clear the")
  if (index >= 0) {
    val start = content.lastIndexOf("\n", index - 1) + 1
    val end = content.indexOf("field.delete_cached_value(self)", index) + "field.delete_cached_value(self)".length
    println(s"Found at positions: $start to $end")
    println(s"Content:\n${content.substring(start, end)}")
  } else {
    println("Not found")
  }
}