
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo") {
  val expr = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo/django/db/models/expressions.py")
  val content = expr.read()
  
  // Find class Ref
  val refClassIdx = content.indexOf("class Ref(Expression):")
  println(s"Ref class at: $refClassIdx")
  
  // Get context around Ref class
  println("Context around Ref class:")
  println(content.substring(refClassIdx, refClassIdx+500))
}
