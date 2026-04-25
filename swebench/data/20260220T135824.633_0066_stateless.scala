
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14997/repo") {
  val ddl = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14997/repo/django/db/backends/ddl_references.py")
  val content = ddl.read()
  val lines = content.split("\n").toList
  val newLines = lines.map { line =>
    if line.contains("sql, params = self.compiler.compile(self.expressions)") then
      "        expressions = self.expressions\n        if self.table:\n            expressions = expressions.relabeled_clone({self.table: None})\n        sql, params = self.compiler.compile(expressions)"
    else
      line
  }
  val newContent = newLines.mkString("\n")
  ddl.write(newContent)
  println("Done")
}
