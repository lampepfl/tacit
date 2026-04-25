requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val relatedFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/db/models/fields/related.py")
  val lines = relatedFile.readLines()
  // Print from line 1145 onwards for ManyToManyField
  lines.drop(1144).take(100).zipWithIndex.foreach { case (line, i) =>
    println(s"${1145 + i}: $line")
  }
}
