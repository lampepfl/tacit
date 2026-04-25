requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo/django/db/models/query.py")
  val content = f.read()
  val lines = content.split("\n").toList
  
  // Modify the distinct method - add _not_support_combined_queries call after docstring
  val newLines = lines.zipWithIndex.map { case (line, idx) =>
    val lineNum = idx + 1
    if (lineNum == 1141) {
      // After the docstring closing """ and before the assert
      line + "\n" + "        self._not_support_combined_queries('distinct')"
    } else {
      line
    }
  }
  
  // Write the modified content back
  val newContent = newLines.mkString("\n")
  f.write(newContent)
}