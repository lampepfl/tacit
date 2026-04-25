requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql/where.py")
  val content = f.read()
  val lines = content.split("\n").toList
  
  // Create new lines with the fix
  val newLines = lines.zipWithIndex.map { case (line, idx) =>
    val lineNum = idx + 1
    if lineNum == 216 && line.contains("if not compiler.connection.features.supports_boolean_expr_in_select_clause") then
      "        if not sql:\n            sql = '1'\n" + line
    else
      line
  }
  
  val newContent = newLines.mkString("\n")
  f.write(newContent)
  println("File updated")
}