requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo") {
  val fs = summon[FileSystem]
  val testsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo/tests/user_commands/tests.py")
  val content = testsFile.read()
  // Search for help-related tests
  content.linesIterator.zipWithIndex.foreach { case (line, idx) =>
    if (line.toLowerCase.contains("help")) {
      println(s"${idx+1}: $line")
    }
  }
}