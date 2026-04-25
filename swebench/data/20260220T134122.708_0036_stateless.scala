def checkTmp3(): String = {
  val fs = requestFileSystem("/tmp") { (fs: FileSystem) => fs }
  val result = access("/tmp")(using fs)
  val children = result.children.take(5).map(_.name).mkString(", ")
  children
}

checkTmp3()