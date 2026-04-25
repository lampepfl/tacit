requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql/where.py")
  val content = f.read()
  val lines = content.split("\n")
  // Print the select_format method
  val startLine = lines.indexWhere(_.contains("def select_format"))
  println("Modified select_format method:")
  for i <- startLine until (startLine + 12) do
    println(lines(i))
}