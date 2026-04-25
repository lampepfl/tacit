requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11179/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  // Read the deletion.py file 
  val deletionFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11179/repo/django/db/models/deletion.py")
  val content = deletionFile.read()
  
  // Let's split by lines 
  val lines = content.split("\n").toList
  
  // Insert the new line after line 279 (index 278)
  val newLine = "                setattr(instance, model._meta.pk.attname, None)"
  
  // More precise - insert after index 278
  val newLines = lines.zipWithIndex.flatMap { case (line, idx) =>
    if idx == 278 then
      List(line, newLine)
    else
      List(line)
  }
  
  val newContent = newLines.mkString("\n")
  deletionFile.write(newContent)
  println("File updated!")
}