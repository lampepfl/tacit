val op = (fs: FileSystem) => {
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo")
  val children = root.children.map((e: FileEntry) => e.name).take(20)
  println(children.mkString(", "))
}

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo")(op)