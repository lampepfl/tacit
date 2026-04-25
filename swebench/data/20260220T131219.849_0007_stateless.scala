
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo/django/db/migrations/operations/models.py")
  val lines = f.readLines()
  
  // Print lines around AlterOrderWithRespectTo (starting at line 572)
  for (i <- 571 until 631) {
    println(s"${i + 1}: ${lines(i)}")
  }
}
