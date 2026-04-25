def myOp(using io: IOCapability) = {
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") {
    val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo/django/db/migrations/operations/models.py")
    val lines = f.readLines()
    // Print more lines to see the full database_forwards method
    lines.slice(318, 380).zipWithIndex.foreach { case (line, idx) =>
      println(s"${idx + 319}: $line")
    }
  }
}
myOp