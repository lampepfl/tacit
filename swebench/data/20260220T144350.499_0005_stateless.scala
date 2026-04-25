import library.*

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15814/repo") {
  val queryFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15814/repo/django/db/models/sql/query.py")
  
  // Read all the content
  val content = queryFile.read()
  
  // Find and replace the specific line
  val oldLine = "                opts = cur_model._meta"
  val newLine = "                opts = cur_model._meta.concrete_model._meta"
  
  val newContent = content.replace(oldLine, newLine)
  
  // Write back
  queryFile.write(newContent)
  
  println("File updated successfully")
}