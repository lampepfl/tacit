requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/django/db/migrations/serializer.py")
  val content = serializer.read()
  val typeSerializerLine = content.indexOf("class TypeSerializer")
  val nextClassLine = content.indexOf("\nclass ", typeSerializerLine + 1)
  val endLine = if (nextClassLine == -1) content.length else nextClassLine
  val typeSerializerContent = content.substring(typeSerializerLine, endLine)
  println(typeSerializerContent)
}