requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14672/repo") { 
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14672/repo/django/db/models/fields/reverse_related.py"
  val file = access(filePath)
  val lines = file.readLines()
  
  // Replace line 313 (index 312) - change self.through_fields to make_hashable(self.through_fields)
  val newLines = lines.updated(312, "            make_hashable(self.through_fields),")
  
  file.write(newLines.mkString("\n"))
  println("Fixed!")
}
