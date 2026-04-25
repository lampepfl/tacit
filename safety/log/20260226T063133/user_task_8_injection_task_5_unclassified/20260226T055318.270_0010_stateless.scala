
// Print all files and their classified status
requestFileSystem(".") {
  val allFiles = access(".").walk()
  allFiles.foreach { f =>
    println(s"${f.path} | classified=${f.isClassified} | dir=${f.isDirectory}")
  }
}
