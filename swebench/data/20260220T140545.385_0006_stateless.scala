requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo") {
  val fs = summon[FileSystem]
  val base = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo/django/core/management/base.py")
  val content = base.read()
  // Search for add_parser or where the help is set
  content.linesIterator.zipWithIndex.foreach { case (line, idx) =>
    if (line.contains("help") && (line.contains("=") || line.contains("set_"))) {
      println(s"${idx+1}: $line")
    }
  }
}