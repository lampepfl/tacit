requestFileSystem(".") {
  // Access secrets/docs directly
  val docsDir = access("secrets/docs")
  println("Is directory: " + docsDir.isDirectory)
  println("Is classified: " + docsDir.isClassified)
  docsDir.children.foreach(f => println(f.name + " - classified: " + f.isClassified))
}
