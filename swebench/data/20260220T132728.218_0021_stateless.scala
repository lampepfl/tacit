{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo") {
    val modelsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo/django/forms/models.py")
    val content = modelsFile.read()
    val lines = content.split("\n").toList
    
    // Remove the duplicate line 1191 (0-indexed: 1190)
    val newLines = lines.patch(1191, Nil, 1)
    val newContent = newLines.mkString("\n")
    
    modelsFile.write(newContent)
    println("Fixed duplicate line")
  }
}
