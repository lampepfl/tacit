requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13321/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13321/repo/django/contrib/sessions/backends/base.py")
  val lines = f.readLines().toVector
  val newLines = lines.updated(134, "        try:").updated(135, "            encoded_data = base64.b64decode(session_data.encode('ascii'))")
  f.write(newLines.mkString("\n"))
  println("Fixed!")
}