requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // Get raw bytes around the area
  val idx = content.indexOf("year = int(m.group('year'))")
  println("Raw bytes around year = int:")
  for i <- idx until idx + 120 do
    print(content(i).toChar)
}