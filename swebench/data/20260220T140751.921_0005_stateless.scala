requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15789/repo") {
  val filterFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15789/repo/django/template/defaultfilters.py")
  val lines = filterFile.readLines()
  // Get lines around the json_script filter
  lines.drop(83).take(12).zipWithIndex.foreach { case (line, idx) => 
    println(s"${idx + 84}: $line") 
  }
}