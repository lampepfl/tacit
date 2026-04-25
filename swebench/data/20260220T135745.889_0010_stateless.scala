
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15320/repo") {
  val f = access("django/db/models/expressions.py")
  val content = f.read()
  val lines = content.split("\n").toList
  
  // Find the __init__ method and add the fix
  val newLines = lines.zipWithIndex.map { case (line, idx) =>
    if idx == 1151 then  // Line 1152 (0-indexed: 1151) - this is the line "self.query = getattr(queryset, 'query', queryset)"
      "        self.query = getattr(queryset, 'query', queryset)\n        self.query.subquery = True"
    else line
  }
  
  val newContent = newLines.mkString("\n")
  f.write(newContent)
  println("Done")
}
