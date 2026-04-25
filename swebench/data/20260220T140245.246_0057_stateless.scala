def myOp(using io: IOCapability) = {
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") {
    val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo/django/db/migrations/operations/models.py")
    val lines = f.readLines()
    // Print lines around RenameModel database_backwards (around line 370)
    lines.slice(368, 385).zipWithIndex.foreach { case (line, idx) =>
      println(s"${idx + 369}: $line")
    }
  }
}
myOp