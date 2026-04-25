
requestFileSystem(".")(fs => 
  val e = access(".")
  e.children.map(f => f.name).mkString(", ")
)
