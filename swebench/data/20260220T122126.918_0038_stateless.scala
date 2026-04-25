requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo/django/db/migrations/autodetector.py")
  val lines = f.readLines().toVector
  
  // The fix: simply remove the buggy line 929, keeping the if structure intact
  // The if block now does nothing, which is correct - we don't want to change 
  // remote_field_name since it's already the correct (new) value
  val newLines = lines.zipWithIndex.flatMap { case (line, idx) =>
    val lineNum = idx + 1  // 1-indexed
    if lineNum == 929 then
      // Remove this line - it's incorrectly overwriting the new name with old name
      None
    else
      Some(line)
  }
  
  val newContent = newLines.mkString("\n")
  f.write(newContent)
  println("File updated successfully")
}