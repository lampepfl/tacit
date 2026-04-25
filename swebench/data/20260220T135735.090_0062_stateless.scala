
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14997/repo") {
  val ddl = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14997/repo/django/db/backends/ddl_references.py")
  val content = ddl.read()
  val old = """    def __str__(self):
        sql, params = self.compiler.compile(self.expressions)
        params = map(self.quote_value, params)
        return sql % tuple(params)"""
  val new = """    def __str__(self):
        expressions = self.expressions
        if self.table:
            expressions = expressions.relabeled_clone({self.table: None})
        sql, params = self.compiler.compile(expressions)
        params = map(self.quote_value, params)
        return sql % tuple(params)"""
  val new_content = content.replace(old, new)
  ddl.write(new_content)
  println("Done")
}
