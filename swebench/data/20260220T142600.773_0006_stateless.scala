val repoPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13230/repo"

def listDir(fs: FileSystem): List[String] = {
  val f = access(repoPath)
  f.children.take(20).map(_.name)
}

requestFileSystem(repoPath)(listDir)