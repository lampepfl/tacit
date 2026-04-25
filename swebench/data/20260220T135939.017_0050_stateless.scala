requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql/where.py")
  val content = f.read()
  
  // Find and replace using exact match
  val oldText = "        if not compiler.connection.features.supports_boolean_expr_in_select_clause:\n            sql = f'CASE WHEN {sql} THEN 1 ELSE 0 END'"
  val newText = "        if not sql:\n            sql = '1'\n        if not compiler.connection.features.supports_boolean_expr_in_select_clause:\n            sql = f'CASE WHEN {sql} THEN 1 ELSE 0 END'"
  
  if content.contains(oldText) then
    val newContent = content.replace(oldText, newText)
    f.write(newContent)
    println("File updated successfully")
  else
    println("Pattern not found, trying different approach")
    // Print the actual content around line 216
    val lines = content.split("\n")
    println(s"Line 216: '${lines(215)}'")
    println(s"Line 217: '${lines(216)}'")
}