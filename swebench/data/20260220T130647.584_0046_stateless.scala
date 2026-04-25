
// Try specifying the type in the second parameter list
requestFileSystem(".")((fs: FileSystem) => fs.access(".").children.map(x => x.name).mkString(", "): String)
