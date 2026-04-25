
requestFileSystem("secrets") {
  // Walk the secrets directory to find the correct paths
  access("secrets").walk().foreach { f =>
    println(s"${f.path}  (classified=${f.isClassified}, dir=${f.isDirectory})")
  }
}
