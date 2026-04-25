val repoPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo"
requestFileSystem(repoPath) { fs =>
  grep("django/urls/resolvers.py", "class ResolverMatch")
}