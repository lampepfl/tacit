requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql/where.py")
  val content = f.read()
  val newContent = content.replace(
    """    def select_format(self, compiler, sql, params):
        # Wrap filters with a CASE WHEN expression if a database backend
        # (e.g. Oracle) doesn't support boolean expression in SELECT or GROUP
        # BY list.
        if not compiler.connection.features.supports_boolean_expr_in_select_clause:
            sql = f'CASE WHEN {sql} THEN 1 ELSE 0 END'
        return sql, params""",
    """    def select_format(self, compiler, sql, params):
        # Wrap filters with a CASE WHEN expression if a database backend
        # (e.g. Oracle) doesn't support boolean expression in SELECT or GROUP
        # BY list.
        if not sql:
            sql = '1'
        if not compiler.connection.features.supports_boolean_expr_in_select_clause:
            sql = f'CASE WHEN {sql} THEN 1 ELSE 0 END'
        return sql, params"""
  )
  f.write(newContent)
  println("File updated successfully")
}