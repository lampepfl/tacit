
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13028/repo") {
  val queryFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13028/repo/django/db/models/sql/query.py")
  val lines = queryFile.readLines()
  
  // Replace lines 1125-1134 (0-indexed: 1124-1133)
  val newLines = lines.updated(1124, "    def check_filterable(self, expression):")
  val newLines2 = newLines.updated(1125, "        \"\"\"Raise an error if expression cannot be used in a WHERE clause.\"\"\"")
  val newLines3 = newLines2.updated(1126, "        if hasattr(expression, 'get_source_expressions'):")
  val newLines4 = newLines3.updated(1127, "            if not getattr(expression, 'filterable', True):")
  val newLines5 = newLines4.updated(1128, "                raise NotSupportedError(")
  val newLines6 = newLines5.updated(1129, "                    expression.__class__.__name__ + ' is disallowed in the filter '")
  val newLines7 = newLines6.updated(1130, "                    'clause.'")
  val newLines8 = newLines7.updated(1131, "                )")
  val newLines9 = newLines8.updated(1132, "            for expr in expression.get_source_expressions():")
  val newLines10 = newLines9.updated(1133, "                self.check_filterable(expr)")
  
  queryFile.write(newLines10.mkString("\n"))
  println("File updated successfully")
}
