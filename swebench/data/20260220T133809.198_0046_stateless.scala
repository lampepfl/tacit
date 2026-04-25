{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo") {
    val modelsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo/django/forms/models.py")
    val content = modelsFile.read()
    val lines = content.split("\n").toList
    
    // Line 1194 (0-indexed: 1193) still has the extra content - remove it
    val newLines = lines.patch(1194, Nil, 1)
    
    val newContent = newLines.mkString("\n")
    modelsFile.write(newContent)
    println("Removed extra line")
  }
}
