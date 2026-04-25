requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo") { 
  val fieldsInit = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo/django/db/models/fields/__init__.py")
  val lines = fieldsInit.readLines()
  
  // Fix the check method to include both checks
  val newLines = lines.zipWithIndex.map { case (line, idx) =>
    val lineNum = idx + 1
    if lineNum == 958 then "            *self._check_max_length_attribute(**kwargs),\n            *self._check_max_length_choices(**kwargs),"
    else line
  }
  
  val newContent = newLines.mkString("\n")
  fieldsInit.write(newContent)
  println("File updated successfully")
}