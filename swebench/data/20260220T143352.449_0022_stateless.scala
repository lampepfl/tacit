requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo/django/utils/functional.py")
  val lines = file.readLines().toList
  
  // Remove lines 439-449 (the second and third __radd__ and the stray return)
  val newLines = lines.patch(439, List(), 10)
  file.write(newLines.mkString("\n"))
}