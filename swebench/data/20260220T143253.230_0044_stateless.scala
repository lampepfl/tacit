requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo") { 
  val autoreload = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo/django/utils/autoreload.py")
  val content = autoreload.read()
  // Fix indentation - replace 10-space indent with 8-space
  val lines = content.split("\n").toList
  val fixed = lines.map { line =>
    if line.startsWith("          ") && !line.startsWith("            ") then
      "    " + line.drop(2)  // Convert 10-space to 8-space
    else
      line
  }
  autoreload.write(fixed.mkString("\n"))
}