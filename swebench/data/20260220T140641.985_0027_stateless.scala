requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo") {
  given fs: FileSystem = summon[FileSystem]
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo/django/db/migrations/operations/models.py")
  val lines = file.readLines()
  lines.slice(995, 1015).zipWithIndex.foreach { case (line, idx) =>
    println(s"${idx + 996}: $line")
  }
}