requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/query_utils.py")
  val content = f.read()
  val lines = content.split("\n").toList
  // Find Q class and its __invert__ method
  val invertIdx = lines.indexWhere(_.contains("def __invert__"))
  println(s"__invert__ at line $invertIdx")
  if invertIdx >= 0 then
    lines.slice(invertIdx, invertIdx + 10).foreach(println)
}