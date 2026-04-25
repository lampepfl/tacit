def listRoot(): List[String] = {
  val repoPath = "/"
  var result: List[String] = Nil
  
  requestFileSystem(repoPath) { (fs: FileSystem) =>
    given FileSystem = fs
    val root = access("/")
    result = root.children.map(_.name).take(20).toList
  }
  
  result
}

listRoot()