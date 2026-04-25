
val f: FileSystem => String = (fs: FileSystem) => access(".").children.map(x => x.name).mkString(", ")

requestFileSystem(".")(f)
