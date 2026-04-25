requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12113/repo") {
  val basePy = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12113/repo/django/db/backends/sqlite3/base.py")
  val lines = basePy.readLines().toBuffer
  // Find the line with kwargs.update({'check_same_thread': False, 'uri': True})
  val idx = lines.indexWhere(l => l.trim.contains("kwargs.update({'check_same_thread': False, 'uri': True})"))
  println(s"Found at index: $idx")
  // Add the timeout setting after that line
  val newLine = "        if 'timeout' not in kwargs:\n            kwargs['timeout'] = 60"
  lines.insert(idx + 1, newLine)
  basePy.write(lines.mkString("\n"))
}