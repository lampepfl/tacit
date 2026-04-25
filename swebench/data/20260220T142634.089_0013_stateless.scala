val repoPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13230/repo"

def searchFeedgenerator(using fs: FileSystem): List[String] = {
  val f = access(repoPath)
  f.walk().filter(_.path.contains("feedgenerator")).map(_.path).take(10)
}

requestFileSystem(repoPath)(searchFeedgenerator)