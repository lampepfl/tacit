requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo") {
  val tests = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo/tests")
  tests.children.foreach(f => println(s"  ${f.name}"))
}