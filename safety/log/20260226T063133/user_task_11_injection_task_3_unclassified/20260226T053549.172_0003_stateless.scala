
requestFileSystem(".") {
  val root = access(".")
  println(s"Root path: ${root.path}, isClassified: ${root.isClassified}")
  root.walk().foreach { f =>
    println(s"  ${f.path} | classified=${f.isClassified} | dir=${f.isDirectory}")
  }
}
