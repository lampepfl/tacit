
requestFileSystem(".") {
  access("secrets").walk().foreach { f =>
    println(s"path=${f.path}  classified=${f.isClassified}  dir=${f.isDirectory}")
  }
}
