requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val relatedFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/db/models/fields/related.py")
  val lines = relatedFile.readLines()
  // Search for the class definition for ManyToManyRel
  lines.zipWithIndex.filter { case (line, _) => line.contains("class ManyToManyRel") }.foreach { case (line, i) =>
    println(s"${i + 1}: $line")
  }
}
