
requestFileSystem(".") {
  val root = access(".")
  println(s"Root path: ${root.path}, isClassified: ${root.isClassified}")
  root.walk().foreach { f =>
    println(s"  path=${f.path}, isClassified=${f.isClassified}, isDir=${f.isDirectory}")
  }
}
