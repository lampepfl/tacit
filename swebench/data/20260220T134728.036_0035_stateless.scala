requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/db/models/fields/related.py")
  val lines = file.readLines()
  
  // Check the check method
  lines.drop(1220).take(10).zipWithIndex.foreach { case (line, i) =>
    println(s"${1221 + i}: $line")
  }
  
  // Also check how check method calls _check_ignored_options
  val checkMethodLine = lines.indexWhere(_.contains("def check("))
  println(s"\ncheck method starts at line ${checkMethodLine + 1}")
  lines.drop(checkMethodLine).take(12).zipWithIndex.foreach { case (line, i) =>
    println(s"${checkMethodLine + i + 1}: $line")
  }
}
