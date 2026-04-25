requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/lookups.py")
  val content = f.read()
  val lines = content.split("\n").toList
  // Look at lines around 417
  println("=== Lines 400-440 ===")
  lines.slice(399, 440).foreach(println)
}