requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo") { 
  val queryPy = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo/django/db/models/sql/query.py")
  val content = queryPy.read()
  
  // Find and replace the buggy line
  val oldCode = """                if inner_query.default_cols and has_existing_aggregate_annotations:
                    inner_query.group_by = (self.model._meta.pk.get_col(inner_query.get_initial_alias()),)"""
  
  val newCode = """                if inner_query.default_cols and has_existing_aggregate_annotations:
                    if inner_query.group_by is None:
                        inner_query.group_by = (self.model._meta.pk.get_col(inner_query.get_initial_alias()),)"""
  
  val newContent = content.replace(oldCode, newCode)
  queryPy.write(newContent)
  println("Done")
}