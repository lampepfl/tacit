requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13220/repo") { 
  val exceptionsPath = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13220/repo/django/core/exceptions.py")
  
  // Read line by line to check indentation
  val lines = exceptionsPath.readLines()
  val idx = lines.indexWhere(_.contains("def __repr__"))
  println(s"Lines around __repr__:")
  lines.slice(idx-10, idx+3).zipWithIndex.foreach { case (line, i) => 
    println(s"${idx-10+i}: |$line|")
  }
}
