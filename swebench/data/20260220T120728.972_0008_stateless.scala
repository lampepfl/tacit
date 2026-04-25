requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // Try to find the exact substring
  val idx = content.indexOf("year = int(m.group('year'))")
  if idx >= 0 then
    println(s"Found at index $idx")
    println(s"Context: '${content.substring(idx, idx+100)}'")
}