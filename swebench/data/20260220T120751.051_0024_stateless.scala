requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo") { 
  val queryPy = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo/django/db/models/sql/query.py")
  val content = queryPy.read()
  
  // Find the exact location
  val searchStr = "if inner_query.default_cols and has_existing_aggregate_annotations:\n                    inner_query.group_by = (self.model._meta.pk.get_col(inner_query.get_initial_alias()),)"
  val idx = content.indexOf(searchStr)
  println(s"Found at: $idx")
  
  if (idx >= 0) {
    val newContent = content.substring(0, idx) + 
      "if inner_query.default_cols and has_existing_aggregate_annotations:\n                    if inner_query.group_by is None:\n                        inner_query.group_by = (self.model._meta.pk.get_col(inner_query.get_initial_alias()),)" +
      content.substring(idx + searchStr.length)
    queryPy.write(newContent)
    println("Done!")
  } else {
    println("Pattern not found")
  }
}