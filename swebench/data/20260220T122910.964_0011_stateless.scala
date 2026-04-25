requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12286/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12286/repo/django/core/checks/translation.py")
  val lines = f.readLines()
  val newLines = lines.zipWithIndex.map { case (line, idx) =>
    if (idx == 58) "        if settings.LANGUAGE_CODE.split('-')[0] not in available_tags:"
    else line
  }
  val newContent = newLines.mkString("\n")
  f.write(newContent)
  println("File updated successfully")
}
