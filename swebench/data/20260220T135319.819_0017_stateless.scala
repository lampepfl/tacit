requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql/compiler.py")
  val content = f.read()
  val lines = content.split("\n").toList
  // Look at lines around 1200
  println("=== Lines 1190-1220 ===")
  lines.slice(1189, 1220).foreach(println)
}