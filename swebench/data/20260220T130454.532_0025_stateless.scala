
// Let me check what methods are available on FileSystem
classOf[FileSystem].getMethods.map(_.getName).distinct.mkString(", ")
