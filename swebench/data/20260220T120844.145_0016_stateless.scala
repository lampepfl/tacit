requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // Use regex or find with context
  val lines = content.split("\n")
  val line176 = lines(176)
  val idx = content.indexOf(line177)
  println(s"Index of line 177: $idx")
  
  // Actually, let's find by line number directly
  // Count newlines to get to the right position
  var lineNum = 0
  var pos = 0
  while lineNum < 176 do
    if content(pos) == '\n' then lineNum += 1
    pos += 1
  println(s"Position of line 176: $pos")
}