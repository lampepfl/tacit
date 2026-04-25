import library.*

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11133/repo") { 
  // Check if there are any other places in django/http that handle content conversion similarly
  val matches = grepRecursive(".", "isinstance.*bytes.*return", "*.py")
  // Filter to only http related files
  matches.filter(m => m.file.contains("http")).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}