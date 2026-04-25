def myOp(using io: IOCapability) = {
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") {
    val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo/django/db/migrations/operations/models.py")
    val content = f.read()
    val lines = content.split("\n").toList
    
    // Find the line with "old_model = from_state.apps.get_model(app_label, self.old_name)"
    val targetLineIndex = lines.indexWhere(_.contains("old_model = from_state.apps.get_model(app_label, self.old_name)"))
    println(s"Target line index: $targetLineIndex")
    println(s"Target line: ${lines(targetLineIndex)}")
    
    // Insert the check after this line
    val newLines = lines.take(targetLineIndex + 1) ++ 
                   List("            # The db_table is already defined, so this is a noop.") ++ 
                   List("            if old_model._meta.db_table == new_model._meta.db_table:") ++ 
                   List("                return") ++ 
                   lines.drop(targetLineIndex + 1)
    
    val newContent = newLines.mkString("\n")
    
    // Write back
    f.write(newContent)
    println("File updated successfully")
  }
}
myOp