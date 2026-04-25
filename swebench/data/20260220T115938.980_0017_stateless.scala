requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11564/repo") {
  val testsDir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11564/repo/tests")
  testsDir.children.filter(_.isDirectory).take(30).foreach(e => println(e.name))
}