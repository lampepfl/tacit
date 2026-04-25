val op = (fs: FileSystem) => {
  given FileSystem = fs
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo")
  val names = new scala.collection.mutable.ListBuffer[String]
  for child <- root.children.take(20) do
    names.addOne(child.name)
  println(names.toList.mkString(", "))
}

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo")(op)