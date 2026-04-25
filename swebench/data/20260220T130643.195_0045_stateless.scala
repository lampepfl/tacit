
// Try using explicit type parameter
requestFileSystem[String](".")( (fs: FileSystem) => fs.access(".").children.map(x => x.name).mkString(", ") )
