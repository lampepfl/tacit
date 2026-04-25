val repoPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13230/repo"
requestFileSystem(repoPath)(fs => 
  access(repoPath).children.take(20).map(_.name)
)