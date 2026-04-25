requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql/where.py")
  val content = f.read()
  val lines = content.split("\n").toList
  // Get the full select_format method with line numbers
  lines.zipWithIndex.slice(212, 225).foreach { case (line, idx) => 
    println(s"${idx+1}: $line")
  }
}