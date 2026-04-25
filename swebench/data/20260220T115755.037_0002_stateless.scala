requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11583/repo") {
  val utilsDir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11583/repo/django/utils")
  utilsDir.children.filter(f => f.name.contains("autoreload")).map(f => f.name)
}