
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15320/repo") {
  val f = access("django/db/models/expressions.py")
  val lines = f.readLines().toVector
  
  // Line 1152 is index 1151 (0-indexed), add after that line
  val newLines = lines.patch(1152, List("        self.query.subquery = True"), 0)
  
  f.write(newLines.mkString("\n"))
  println("Done")
}
