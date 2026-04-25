
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo") {
  val expr = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo/django/db/models/expressions.py")
  val content = expr.read()
  
  // Find position 29243 and get more context
  val pos = 29243
  println(s"Char at $pos: '${content.charAt(pos)}'")
  println(s"Substring from $pos to ${pos+100}:")
  println(content.substring(pos, pos+100))
}
