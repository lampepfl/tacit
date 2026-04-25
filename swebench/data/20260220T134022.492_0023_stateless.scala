val repoPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo"

val op = requestFileSystem(repoPath) { (fs: FileSystem) =>
  given FileSystem = fs
  val f = access(repoPath + "/django/urls/resolvers.py")
  f.exists
}

op match
  case f: Function0[_] => f()
  case other => other