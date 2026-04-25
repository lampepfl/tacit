requestFileSystem(".") {
  // Let's see what's in the current directory
  val root = access(".")
  println("Contents of current directory:")
  root.children.foreach { f =>
    println(s"  ${f.name} (isClassified=${f.isClassified}, isDir=${f.isDirectory})")
  }
}