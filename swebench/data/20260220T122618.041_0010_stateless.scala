requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  val serializerFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/django/db/migrations/serializer.py")
  val lines = serializerFile.readLines()
  // Print lines from around 320 to 400
  lines.drop(320).take(80).zipWithIndex.map { case (line, idx) => s"${idx + 320}: $line" }
}