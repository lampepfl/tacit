
def listDir(path: String): String = requestFileSystem(path)(fs => 
  access(path).children.map(f => s"${f.name}").mkString(", ")
)

listDir(".")
