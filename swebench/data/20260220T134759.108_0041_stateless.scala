requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val testsDir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/tests")
  val children = testsDir.children.filter(_.isDirectory).toList
  children.filter(_.name.contains("field")).foreach(f => println(s"${f.name}"))
}
