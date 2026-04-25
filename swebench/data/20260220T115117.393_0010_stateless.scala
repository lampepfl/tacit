requestFileSystem("/tmp") { (fs: FileSystem) => 
  access("/tmp")(using fs).name
}