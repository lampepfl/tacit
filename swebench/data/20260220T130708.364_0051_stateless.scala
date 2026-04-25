
// Let's explore the django directory
requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django").children.map(x => x.name).mkString(", "))
