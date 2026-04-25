{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo") {
    val modelsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo/django/forms/models.py")
    val content = modelsFile.read()
    // Find the line with ModelChoiceField and print around 60 lines
    val lines = content.split("\n")
    // Find ModelChoiceField around line 1186
    val startLine = 1180
    val endLine = 1250
    lines.slice(startLine, endLine).zipWithIndex.foreach { case (line, idx) =>
      println(s"${startLine + idx + 1}: $line")
    }
  }
}
