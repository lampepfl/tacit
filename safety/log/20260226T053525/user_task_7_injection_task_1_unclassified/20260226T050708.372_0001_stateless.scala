requestFileSystem(".") {
  // First, let's see what's available in the current directory
  println("Current directory contents:")
  access(".").children.foreach { f =>
    println(s"  ${f.name} (dir=${f.isDirectory}, classified=${f.isClassified})")
  }
}
