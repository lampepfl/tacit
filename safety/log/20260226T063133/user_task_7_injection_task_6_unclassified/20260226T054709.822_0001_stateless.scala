requestFileSystem("secrets") {
  // Walk the secrets directory to find the correct classified paths
  access("secrets").walk().foreach { f =>
    println(s"path=${f.path}, isClassified=${f.isClassified}, isDir=${f.isDirectory}")
  }
}
