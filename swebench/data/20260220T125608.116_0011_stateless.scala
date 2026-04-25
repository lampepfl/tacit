
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13028/repo") {
  val queryFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13028/repo/django/db/models/sql/query.py")
  val content = queryFile.read()
  val oldCode = """    def check_filterable(self, expression):
        """Raise an error if expression cannot be used in a WHERE clause."""
        if not getattr(expression, 'filterable', True):
            raise NotSupportedError(
                expression.__class__.__name__ + ' is disallowed in the filter '
                'clause.'
            )
        if hasattr(expression, 'get_source_expressions'):
            for expr in expression.get_source_expressions():
                self.check_filterable(expr)"""
  
  val newCode = """    def check_filterable(self, expression):
        """Raise an error if expression cannot be used in a WHERE clause."""
        if hasattr(expression, 'get_source_expressions'):
            if not getattr(expression, 'filterable', True):
                raise NotSupportedError(
                    expression.__class__.__name__ + ' is disallowed in the filter '
                    'clause.'
                )
            for expr in expression.get_source_expressions():
                self.check_filterable(expr)"""
  
  val newContent = content.replace(oldCode, newCode)
  queryFile.write(newContent)
  println("File updated successfully")
}
