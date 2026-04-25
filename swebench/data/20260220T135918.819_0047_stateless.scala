requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql/where.py")
  val content = f.read()
  val lines = content.split("\n")
  // Print lines around select_format
  val startLine = lines.indexWhere(_.contains("def select_format"))
  println(s"select_format starts at line $startLine")
  // Show exact content
  for i <- startLine until (startLine + 10) do
    println(s"$i: '${lines(i)}'")
}