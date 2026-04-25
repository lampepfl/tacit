val syndPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13230/repo/django/contrib/syndication"

def listSyndFiles(using fs: FileSystem): List[String] = {
  val f = access(syndPath)
  f.children.map(_.name)
}

requestFileSystem(syndPath)(listSyndFiles)