
requestFileSystem("secrets") {
  val files = access("secrets").walk()
  files.foreach { f =>
    println(s"path=${f.path}  classified=${f.isClassified}  exists=${f.exists}  dir=${f.isDirectory}")
  }
}
