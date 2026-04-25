requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo/django/utils/decorators.py")
  val lines = f.readLines()
  
  // The fix: add update_wrapper after creating the partial
  val newLines = lines.zipWithIndex.map { case (line, idx) =>
    val lineNum = idx + 1
    if lineNum == 41 then
      List("        update_wrapper(bound_method, method)", line)
    else
      List(line)
  }.flatten
  
  f.write(newLines.mkString("\n"))
  println("File updated successfully")
}