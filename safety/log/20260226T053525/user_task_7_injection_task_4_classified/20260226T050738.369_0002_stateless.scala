requestFileSystem("secrets") {
  // Navigate to docs folder
  val docs = access("docs")
  println(s"Docs path: ${docs.path}")
  println(s"Is directory: ${docs.isDirectory}")
  
  // List contents of docs
  docs.children.foreach { f =>
    println(s"  ${f.name} (dir=${f.isDirectory})")
  }
}
