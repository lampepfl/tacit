
requestFileSystem(".")(fs => {
  find(".", "*").mkString("\n")
})
