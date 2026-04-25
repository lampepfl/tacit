requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { 
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo")
  val children = root.children.filter(f => f.name == "astropy")
  if children.nonEmpty then
    val astropy = children.head
    val ioChildren = astropy.children.filter(f => f.name == "io")
    if ioChildren.nonEmpty then
      val io = ioChildren.head
      val asciiChildren = io.children.filter(f => f.name == "ascii")
      if asciiChildren.nonEmpty then
        val ascii = asciiChildren.head
        val files = ascii.children.map(f => f.name)
        println(files.mkString(", "))
}