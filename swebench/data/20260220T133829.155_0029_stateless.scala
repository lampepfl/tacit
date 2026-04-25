requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  val tests = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo/tests")
  tests.children.take(20).foreach(f => println(f.name))
}