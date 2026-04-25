requestFileSystem(".") {
  // Check what exists in the secrets directory
  println("Checking secrets directory structure:")
  val docsDir = access("secrets/docs")
  println(s"docs dir exists: ${docsDir.exists}, isDirectory: ${docsDir.isDirectory}")
  
  if (docsDir.exists && docsDir.isDirectory) {
    docsDir.children.foreach { f =>
      println(s"  - ${f.name}, classified: ${f.isClassified}")
    }
  }
}