
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo") {
  val expr = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo/django/db/models/expressions.py")
  val content = expr.read()
  
  // Get exact strings
  val idx = content.indexOf("class Ref(Expression):")
  val afterRef = content.substring(idx)
  val methodIdx = afterRef.indexOf("def get_group_by_cols")
  val fullIdx = idx + methodIdx
  
  // Get the exact method and next class
  val methodStart = fullIdx
  val nextClassStart = content.indexOf("\nclass ExpressionList", methodStart)
  println(s"Method starts at: $methodStart, next class at: $nextClassStart")
  
  val oldMethod = content.substring(methodStart, nextClassStart)
  println(s"Old method:\n$oldMethod")
}
