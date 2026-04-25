import library.*

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11133/repo") { 
  val responseFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11133/repo/django/http/response.py")
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11133/repo/django/http/response.py", "isinstance.*memoryview")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}