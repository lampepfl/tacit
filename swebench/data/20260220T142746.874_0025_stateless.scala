val docPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13230/repo/docs/ref/contrib/syndication.txt"

def readDocLines(using fs: FileSystem): List[String] = {
  val lines = access(docPath).readLines()
  lines.slice(960, 990)
}

requestFileSystem(docPath)(readDocLines)