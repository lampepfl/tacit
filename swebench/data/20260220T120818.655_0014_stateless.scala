requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // Find the exact substring
  val idx = content.indexOf("year = int(m.group('year'))")
  val substring = content.substring(idx, idx + 200)
  println(s"Substring: [$substring]")
  println(s"Equals old: ${substring == "        year = int(m.group('year'))"}")
}