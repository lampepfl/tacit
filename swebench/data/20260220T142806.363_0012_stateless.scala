requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/django/db/migrations/serializer.py")
  val content = serializer.read()
  // Find the exact line with __name__ in TypeSerializer
  val lines = content.split("\n")
  for (i, line) <- lines.zipWithIndex do
    if i >= 259 && i <= 275 then println(s"$i: |$line|")
}