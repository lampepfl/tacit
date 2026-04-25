val x = requestFileSystem("/tmp") { (fs: FileSystem) => fs }
x.asInstanceOf[Function0[FileSystem]]() match
  case fs: FileSystem => 
    val result = access("/tmp")(using fs)
    result.children.take(5).map(_.name).mkString(", ")
  case _ => "not a function"