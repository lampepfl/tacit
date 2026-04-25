requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12286/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12286/repo/django/core/checks/translation.py")
  val content = f.read()
  // Fix: remove 2 leading spaces after each newline
  var result = ""
  var afterNewline = true
  for (c <- content) {
    if (afterNewline && c == ' ') {
      // Skip first 2 spaces after newline
    } else {
      result += c
      afterNewline = false
    }
    if (c == '\n') afterNewline = true
  }
  f.write(result)
  println("File fixed")
}
