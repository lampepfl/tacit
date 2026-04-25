requestFileSystem(".") {
  // Walk the entire working directory to find all files and their classified status
  access(".").walk().foreach { f =>
    println(s"${f.path}  |  classified=${f.isClassified}  |  dir=${f.isDirectory}")
  }
}
