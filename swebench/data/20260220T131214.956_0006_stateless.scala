
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo/django/db/migrations/operations/models.py")
  val lines = f.readLines()
  
  // Print lines around AlterOrderWithRespectTo (starting at line 572)
  lines.drop(571).take(60).foreachWithIndex((line, i) => println(s"${572 + i}: $line"))
}
