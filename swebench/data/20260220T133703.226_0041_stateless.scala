{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo") {
    val modelsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo/django/forms/models.py")
    val content = modelsFile.read()
    val lines = content.split("\n").toList
    
    // Remove line 1194 (0-indexed: 1193)
    val newLines1 = lines.patch(1194, Nil, 1)
    
    // Fix line 1194 (was 1195) - add the def __init__ prefix
    val currentLine = newLines1(1194)
    val fixedLine = "    def __init__" + currentLine.dropWhile(c => c != '(')
    val newLines2 = newLines1.updated(1194, fixedLine)
    
    // Add blank line before it
    val blankLine :: rest = newLines2.drop(1194)
    val newLines3 = newLines2.take(1194) ++ List("") ++ newLines2.drop(1194)
    
    val newContent = newLines3.mkString("\n")
    modelsFile.write(newContent)
    println("Fixed")
  }
}
