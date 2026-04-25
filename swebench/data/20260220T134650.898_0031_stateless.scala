requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/db/models/fields/related.py")
  val lines = file.readLines()
  
  // Check the __init__ modification
  println("=== Checking __init__ modification (around line 1198) ===")
  lines.drop(1195).take(15).zipWithIndex.foreach { case (line, i) =>
    println(s"${1196 + i}: $line")
  }
  
  println("\n=== Checking _check_ignored_options modification ===")
  lines.drop(1229).take(40).zipWithIndex.foreach { case (line, i) =>
    println(s"${1230 + i}: $line")
  }
}
