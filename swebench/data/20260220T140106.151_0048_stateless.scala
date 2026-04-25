def myOp(using io: IOCapability) = {
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") {
    val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo/django/db/migrations/operations/models.py")
    val lines = f.readLines()
    // Print lines around AlterModelTable (starting at line 416, index 415)
    lines.slice(413, 470).zipWithIndex.foreach { case (line, idx) =>
      println(s"${idx + 414}: $line")
    }
  }
}
myOp