
requestFileSystem(".")( (fs: FileSystem) => {
  find(".", "*").mkString("\n")
})
