requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13590/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13590/repo/django/db/models/sql/query.py")
  val content = f.read()
  val lines = content.split("\n").toList
  // Fix: change line 1079 to use * to unpack
  val fixedLines = lines.updated(1079, "            return type(value)(*(")
  // Also need to add closing paren for the *() call
  val fixedLines2 = fixedLines.updated(1082, "            ))")
  val newContent = fixedLines2.mkString("\n")
  f.write(newContent)
  println("File updated")
}