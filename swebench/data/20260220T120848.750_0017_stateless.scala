requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11905/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11905/repo/django/db/models/lookups.py")
  val content = f.read()
  val lines = content.split("\n").toList
  
  // The fix: add __init__ method with validation 
  // We need to insert after line 463 (prepare_rhs = False) 
  // and before line 465 (def as_sql)
  
  val newLines = lines.zipWithIndex.flatMap { case (line, idx) =>
    val lineNum = idx + 1
    if lineNum == 463 && lines(idx).trim == "prepare_rhs = False" then
      List(
        line,
        "",
        "    def __init__(self, lhs, rhs):",
        "        if not isinstance(rhs, bool):",
        "            raise ValueError(",
        "                'The QuerySet value for an isnull lookup must be a bool value.'",
        "            )",
        "        super().__init__(lhs, rhs)"
      )
    else
      List(line)
  }
  
  // Show the modified section
  println("Modified section:")
  newLines.slice(458, 480).foreach(println)
}