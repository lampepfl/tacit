object FileChecker:
  def checkTmp(): String =
    var result = ""
    requestFileSystem("/tmp") { (fs: FileSystem) =>
      given FileSystem = fs
      val f = access("/tmp")
      result = f.children.take(5).map(_.name).mkString(", ")
    }
    result

FileChecker.checkTmp()