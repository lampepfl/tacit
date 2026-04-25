requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14672/repo") { 
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14672/repo/django/db/models/fields/reverse_related.py"
  val file = access(filePath)
  val lines = file.readLines()
  
  // Print lines around 308-318 to verify the fix
  for (i <- 308 until 318) {
    println(s"${i+1}: ${lines(i)}")
  }
}
