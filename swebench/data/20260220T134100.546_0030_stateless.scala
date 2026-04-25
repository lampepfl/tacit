def explorePaths(): String = {
  val paths = List(
    "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace",
    "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941",
    "/Users/tacit/Work/SafeExecMCP/bench",
    "/Users/tacit/Work/SafeExecMCP",
    "/Users/tacit/Work"
  )
  
  var result = ""
  
  requestFileSystem("/Users") { (fs: FileSystem) =>
    given FileSystem = fs
    for p <- paths do
      try
        val f = access(p)
        result += s"$p: exists=${f.exists}, isDir=${f.isDirectory}\n"
      catch
        case e: Exception => result += s"$p: ERROR - ${e.getMessage}\n"
  }
  
  result
}

explorePaths()