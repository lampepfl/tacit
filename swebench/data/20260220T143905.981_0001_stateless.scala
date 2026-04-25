import library.*

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15814/repo") {
  val queryFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15814/repo/django/db/models/sql/query.py")
  
  val lines = queryFile.readLines()
  // Get lines around 745 (0-indexed so 744)
  for i <- 740 to 750 do
    println(s"${i+1}: ${lines(i)}")
}