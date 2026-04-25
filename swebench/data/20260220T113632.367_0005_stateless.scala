def listDir(rootPath: String)(using fs: FileSystem): List[String] = {
  val root = access(rootPath)
  root.children.map((e: FileEntry) => e.name).take(20)
}

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo")(fs => {
  val result = listDir("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo")(using fs)
  println(result.mkString(", "))
})