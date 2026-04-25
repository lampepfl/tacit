val syndPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13230/repo/django/contrib/syndication/views.py"

def readLinesAround(using fs: FileSystem): List[String] = {
  val lines = access(syndPath).readLines()
  // Get lines from 180 to 250 to see the add_item call
  lines.slice(180, 260)
}

requestFileSystem(syndPath)(readLinesAround)