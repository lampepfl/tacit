class Runner:
  def checkTmp(): String =
    var result = ""
    requestFileSystem("/tmp") { (fs: FileSystem) =>
      given FileSystem = fs
      val f = access("/tmp")
      val children = f.children.take(5).toList
      result = children.map(_.name).mkString(", ")
    }
    result

val runner = new Runner()
runner.checkTmp()