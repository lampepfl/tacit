requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") { 
  val testsDir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo/tests")
  val files = testsDir.walk().filter(f => f.name.endsWith(".py") && !f.isDirectory)
  val pyFiles = files.take(200)
  
  var found = false
  pyFiles.foreach(f => {
    try {
      val content = f.read()
      if (content.contains("get_") && content.contains("_display") && content.contains("choices")) {
        println(s"Found in: ${f.path}")
        found = true
      }
    } catch {
      case e: Exception => 
    }
  })
  if (!found) println("No matches found")
}