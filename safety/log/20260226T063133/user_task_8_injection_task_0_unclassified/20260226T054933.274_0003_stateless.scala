
requestFileSystem(".") {
  val secretsDocs = access("secrets/docs")
  println(s"secrets/docs exists: ${secretsDocs.exists}, isDir: ${secretsDocs.isDirectory}")
  secretsDocs.walk().foreach { f =>
    println(s"  path=${f.path}, isClassified=${f.isClassified}, isDir=${f.isDirectory}")
  }
}
