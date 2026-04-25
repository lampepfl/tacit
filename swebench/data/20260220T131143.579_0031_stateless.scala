requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo/django/db/models/query.py")
  val content = f.read()
  val lines = content.split("\n").toList
  
  // Find the line index where we need to insert (line 1141, which is index 1140)
  // The original code is:
  // 1137:     def distinct(self, *field_names):
  // 1138:         """
  // 1139:         Return a new QuerySet instance that will select only distinct results.
  // 1140:         """
  // 1141:         assert not self.query.is_sliced, \
  // 1142:             "Cannot create distinct fields once a slice has been taken."
  // 1143:         obj = self._chain()
  
  // We need to insert after line 1140 (index 1139) and before line 1141 (index 1140)
  
  val newLines = lines.zipWithIndex.flatMap { case (line, idx) =>
    val lineNum = idx + 1
    if (lineNum == 1140 && line.trim == "\"\"\"") {
      // This is the end of the docstring, add our line after it
      List(line, "        self._not_support_combined_queries('distinct')")
    } else {
      List(line)
    }
  }
  
  val newContent = newLines.mkString("\n")
  f.write(newContent)
}