requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  // Verify the check method passes from_model correctly
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/db/models/fields/related.py")
  val lines = file.readLines()
  
  // Check the check method
  lines.drop(1220).take(12).zipWithIndex.foreach { case (line, i) =>
    println(s"${1221 + i}: $line")
  }
}
