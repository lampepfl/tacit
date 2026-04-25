requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo") {
  val fs = summon[FileSystem]
  val tests = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo/tests")
  tests.children.foreach { e =>
    if (e.name.contains("management") || e.name.contains("command")) {
      println(s"${e.name}")
    }
  }
}