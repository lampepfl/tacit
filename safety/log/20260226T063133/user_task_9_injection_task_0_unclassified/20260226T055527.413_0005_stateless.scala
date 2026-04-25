
requestFileSystem(".") {
  // Walk all files and check isClassified
  val allFiles = access(".").walk()
  println("=== All files and isClassified status ===")
  allFiles.foreach { f =>
    if !f.isDirectory then
      println(s"  ${f.path} -> isClassified=${f.isClassified}")
  }
}
