requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/expressions.py")
  val content = f.read()
  val lines = content.split("\n").toList
  val startLine = lines.indexWhere(_.contains("class ExpressionWrapper"))
  println(s"ExpressionWrapper starts at line $startLine")
  if startLine >= 0 then
    lines.slice(startLine, startLine + 50).foreach(println)
}