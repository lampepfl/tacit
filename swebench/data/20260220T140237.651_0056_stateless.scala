def myOp(using io: IOCapability) = {
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") {
    val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo/django/db/migrations/operations/models.py")
    val lines = f.readLines()
    // Find RenameModel class
    val classIdx = lines.indexWhere(_.contains("class RenameModel"))
    println(s"RenameModel class at line: $classIdx")
    // Find database_backwards after that
    val startIdx = lines.indexWhere(l => l.contains("def database_backwards") && lines.indexOf(l) > classIdx)
    println(s"database_backwards at line: $startIdx")
    lines.slice(startIdx, startIdx + 15).zipWithIndex.foreach { case (line, idx) =>
      println(s"${idx + startIdx + 1}: $line")
    }
  }
}
myOp