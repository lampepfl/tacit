requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11179/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  // Read the deletion.py file 
  val deletionFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11179/repo/django/db/models/deletion.py")
  val content = deletionFile.read()
  
  // Let's split by lines and find the exact line to modify
  val lines = content.split("\n").toList
  
  // Find line 279 (index 278) which has the return statement
  // and add the setattr line before it
  val newLines = for (line, idx) <- lines.zipWithIndex yield
    if idx == 278 && line.trim.startsWith("return") then
      s"                setattr(instance, model._meta.pk.attname, None)\n$line"
    else
      line
  
  val newContent = newLines.mkString("\n")
  deletionFile.write(newContent)
  println("File updated!")
}