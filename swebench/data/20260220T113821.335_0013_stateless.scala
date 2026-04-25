val op = (fs: FileSystem) => {
  given FileSystem = fs
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo/astropy/io/ascii")
  val children = root.children.toList
  val names = children.map(e => e.name)
  names
}

val r = requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo")(op)
r