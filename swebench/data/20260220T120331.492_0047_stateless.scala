requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  // Get the exact position
  val idx = content.indexOf("except ValueError:")
  println(s"Found at: $idx")
  // Show raw
  val raw = content.slice(idx - 30, idx + 100)
  raw.foreach(c => print(if c == ' ' then "·" else c.toString))
}