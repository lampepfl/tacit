requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12286/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12286/repo/django/core/checks/translation.py")
  val content = f.read()
  // Remove leading spaces from each line
  val lines = content.split("\n").map(_.dropWhile(_ == ' '))
  val fixedContent = lines.mkString("\n")
  f.write(fixedContent)
  println("File fixed")
}
