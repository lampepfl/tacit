requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo") { 
  val autoreload = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo/django/utils/autoreload.py")
  val content = autoreload.read()
  
  // Count lines to ensure no major issues
  val lines = content.split("\n")
  var inMethod = false
  var methodIndent = 0
  var classDefLine = -1
  var methodDefLine = -1
  
  // Simple syntax check - look for class and method definitions
  lines.zipWithIndex.foreach { (line, idx) =>
    if line.trim.startsWith("class ") then
      classDefLine = idx
    if line.trim.startsWith("def ") && !line.trim.contains("lambda") then
      methodDefLine = idx
  }
  
  println(s"Last class definition at line: $classDefLine")
  println(s"Last method definition at line: $methodDefLine")
  println(s"Total lines: ${lines.length}")
}