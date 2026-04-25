requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // Find the exact substring
  val idx = content.indexOf("year = int(m.group('year'))")
  val firstChar = content(idx)
  println(s"First char code: ${firstChar.toInt}, char: '$firstChar'")
  
  // Let's also verify the lines array version
  val lines = content.split("\n")
  val line176 = lines(176)
  println(s"Line 176 starts with: ${line176.charAt(0).toInt}")
  println(s"Line 176 equals expected: ${line176 == "        year = int(m.group('year'))"}")
}