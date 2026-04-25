requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/expressions.py")
  val content = f.read()
  val lines = content.split("\n").toList
  // Find resolve_expression method in ExpressionWrapper
  val resolveIdx = lines.indexWhere(l => l.contains("def resolve_expression") && lines.indexOf(l) > 955 && lines.indexOf(l) < 1000)
  println(s"resolve_expression around line $resolveIdx")
  if resolveIdx >= 0 then
    lines.slice(resolveIdx, resolveIdx + 30).foreach(println)
}