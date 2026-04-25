
// Let's try using a method definition instead of a lambda
def listDir(): String = {
  requestFileSystem(".")(fs => 
    access(".").children.map(x => x.name).mkString(", ")
  )
}

listDir()
