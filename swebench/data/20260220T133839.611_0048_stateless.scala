{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo") {
    val modelsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo/django/forms/models.py")
    val content = modelsFile.read()
    val lines = content.split("\n").toList
    
    // Find the exact index of the problematic line
    // Look for line containing "available choices" after line 1193
    val problemLineIdx = lines.indexWhere(line => line.contains("available choices") && line.startsWith("                            "), 1193)
    println(s"Problem line index: $problemLineIdx, content: '${lines(problemLineIdx)}'")
    
    // Remove that line
    if (problemLineIdx > 0) {
      val newLines = lines.patch(problemLineIdx, Nil, 1)
      val newContent = newLines.mkString("\n")
      modelsFile.write(newContent)
      println("Removed problem line")
    }
  }
}
