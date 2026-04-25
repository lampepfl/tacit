requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12708/repo") {
  val schemaFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12708/repo/django/db/backends/base/schema.py")
  val content = schemaFile.read()
  val lines = content.split("\n").toList
  
  // Find the _delete_composed_index method from line 401
  val startLine = 400  // 0-indexed, so line 401 is index 400
  val context = lines.slice(startLine, startLine + 20).mkString("\n")
  println("Context:")
  println(context)
}