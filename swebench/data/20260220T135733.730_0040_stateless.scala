requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/sql/query.py")
  val content = f.read()
  val lines = content.split("\n").toList
  // Check if build_filter catches EmptyResultSet 
  // Look for EmptyResultSet in build_filter
  val hasEmptyResultSet = lines.slice(1240, 1340).exists(_.contains("EmptyResultSet"))
  println(s"build_filter handles EmptyResultSet: $hasEmptyResultSet")
  // Also check resolve_expression for Q in expressions
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models", "def resolve_expression", "*.py")
  // Filter to find the one in expressions.py
  val exprMatches = matches.filter(_.file.contains("expressions.py"))
  exprMatches.take(5).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}