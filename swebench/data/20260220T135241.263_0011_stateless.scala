requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/expressions.py")
  val content = f.read()
  val lines = content.split("\n")
  // Find ExpressionWrapper class
  var inExprWrapper = false
  var startLine = -1
  for (i, line) <- lines.zipWithIndex do
    if line.contains("class ExpressionWrapper") then
      startLine = i
      inExprWrapper = true
  println(s"ExpressionWrapper starts at line $startLine")
  // Print around ExpressionWrapper
  if startLine >= 0 then
    lines.slice(startLine, startLine + 50).foreach(println)
}