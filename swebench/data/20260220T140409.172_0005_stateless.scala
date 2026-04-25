requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo") {
  given fs: FileSystem = summon[FileSystem]
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo/tests/migrations/test_operations.py")
  val lines = file.readLines()
  lines.slice(2965, 3020).zipWithIndex.foreach { case (line, idx) =>
    println(s"${idx + 2966}: $line")
  }
}