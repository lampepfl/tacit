requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo/django/db/models/query_utils.py")
  val content = file.read()
  val lines = content.split("\n")
  
  // Fix indentation of comments
  val newLines = lines.map { line =>
    if line == "# If the other Q() is empty, ignore it and just use a copy of `self`." then "        # If the other Q() is empty, ignore it and just use a copy of `self`."
    else if line == "# Or if this Q is empty, ignore it and just use a copy of `other`." then "        # Or if this Q is empty, ignore it and just use a copy of `other`."
    else line
  }
  
  file.write(newLines.mkString("\n"))
  println("Fixed indentation")
}