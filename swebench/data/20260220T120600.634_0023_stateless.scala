requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo") { 
  val fieldsInit = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo/django/db/models/fields/__init__.py")
  val content = fieldsInit.read()
  val lines = content.split("\n").toList
  
  // Fix the issues:
  // 1. Add blank line before _check_max_length_attribute (line 959 -> 960)
  // 2. Fix indentation of _check_max_length_choices (lines 981-1020)
  
  val fixedLines = lines.map { line =>
    if line.startsWith("      def _check_max_length_choices") =>
      "    def _check_max_length_choices(self):"
    else if line.startsWith("          ") && line.trim.nonEmpty =>
      "    " + line.trim
    else if line.startsWith("          ") && line.trim.isEmpty =>
      ""
    else
      line
  }.patch(959, List(""), 0)  // Add blank line at index 959
  
  val newContent = fixedLines.mkString("\n")
  fieldsInit.write(newContent)
  println("File fixed successfully")
}