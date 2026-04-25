requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/expressions.py")
  val content = f.read()
  val lines = content.split("\n").toList
  // Look at select_format method in BaseExpression
  println("=== Lines 380-400 ===")
  lines.slice(379, 400).foreach(println)
}