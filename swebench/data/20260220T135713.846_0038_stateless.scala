requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  // Find where Q is resolved as expression
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/query_utils.py")
  val content = f.read()
  val lines = content.split("\n").toList
  // Look at resolve_expression method and conditional attribute
  println("=== Lines 55-90 ===")
  lines.slice(54, 90).foreach(println)
}