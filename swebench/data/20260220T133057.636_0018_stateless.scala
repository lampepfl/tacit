requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo/django/db/models/query_utils.py")
  val content = file.read()
  val lines = content.split("\n")
  
  // Show lines 44-60 to see the exact content
  lines.slice(43, 60).zipWithIndex.foreach { case (line, idx) =>
    println(s"${idx + 44}: $line")
  }
}