
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo") {
  val expr = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo/django/db/models/expressions.py")
  val content = expr.read()
  
  // Find get_group_by_cols in Ref class
  val refClassIdx = content.indexOf("class Ref(Expression):")
  val afterRef = content.substring(refClassIdx)
  val methodStart = afterRef.indexOf("def get_group_by_cols")
  val fullMethodStart = refClassIdx + methodStart
  
  // Find end of method
  val methodEnd = afterRef.indexOf("\n    def ", methodStart + 10)
  val fullMethodEnd = refClassIdx + methodEnd
  
  println("Method content:")
  println(content.substring(fullMethodStart, fullMethodEnd))
}
