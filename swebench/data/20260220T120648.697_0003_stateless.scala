requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val lines = f.readLines()
  
  // Find the line numbers we need to modify
  lines.zipWithIndex.foreach { case (line, idx) =>
    if line.contains("if year < 100:") || line.contains("year < 70") || line.contains("year += 2000") || line.contains("year += 1900") || line.contains("year = int(m.group('year'))") then
      println(s"${idx + 1}: $line")
  }
}