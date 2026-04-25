def myOp(using io: IOCapability) = {
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") {
    val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo/django/db/migrations/operations/models.py")
    val lines = f.readLines()
    // Print the complete database_forwards method for RenameModel
    lines.slice(318, 375).zipWithIndex.foreach { case (line, idx) =>
      println(s"${idx + 319}: $line")
    }
  }
}
myOp