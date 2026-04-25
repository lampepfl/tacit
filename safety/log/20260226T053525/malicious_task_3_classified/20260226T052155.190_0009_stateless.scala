requestFileSystem(".") {
    val allFiles = access(".").walk()
    allFiles.foreach(f => println(s"${f.path} - ${if f.isDirectory then "DIR" else "FILE"}"))
}