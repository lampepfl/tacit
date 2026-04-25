requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11905/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11905/repo/django/db/models/lookups.py")
  val content = f.read()
  val lines = content.split("\n").toList
  
  // Find the IsNull class (around line 460-471)
  val newLines = lines.zipWithIndex.map { case (line, idx) =>
    val lineNum = idx + 1
    // Around line 461-463 (class definition and lookup_name, prepare_rhs)
    if lineNum == 463 && lines(idx-1).trim == "lookup_name = 'isnull'" then
      // Insert validation after prepare_rhs = False
      line + "\n" + "        if not isinstance(rhs, bool):\n            raise ValueError(\n                'The QuerySet value for an isnull lookup must be a bool value.'\n            )"
    else
      line
  }
  
  // Show the modified section
  println("Modified section:")
  newLines.slice(458, 475).foreach(println)
}