val root = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11039/repo"
requestFileSystem(root) { (fs: FileSystem) => 
  val entry = access(root)(using fs)
  val subs = entry.children
  subs.map(_.name).filter(_.contains("django")).take(5)
}