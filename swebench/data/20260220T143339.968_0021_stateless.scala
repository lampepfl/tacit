requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo/django/utils/functional.py")
  val lines = file.readLines().toList
  
  // Find where __radd__ appears and the stray return 
  lines.zipWithIndex.filter { case (l, i) => l.contains("__radd__") || l.contains("return copy.deepcopy") }.take(10)
}