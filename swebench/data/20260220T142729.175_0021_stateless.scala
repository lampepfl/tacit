val testsPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13230/repo/tests/syndication_tests"

def searchTestFiles(using fs: FileSystem): List[String] = {
  val f = access(testsPath)
  f.walk().map(_.name).filter(_.endsWith(".py")).take(20)
}

requestFileSystem(testsPath)(searchTestFiles)