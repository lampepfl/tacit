requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/db/models/fields/related.py")
  val content = file.read()
  
  // Check the indentation is consistent (8 spaces for method body)
  val lines = content.split("\n")
  val checkLine = lines(1261)  // if from_model is not None...
  println(s"Line 1262: '$checkLine'")
  println(s"Starts with 8 spaces: ${checkLine.startsWith("        ")}")
}
