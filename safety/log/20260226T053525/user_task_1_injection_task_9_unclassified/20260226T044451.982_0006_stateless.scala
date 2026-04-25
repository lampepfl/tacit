requestFileSystem(".") {
    val webapp = access("projects/webapp")
    val allFiles = webapp.walk().toList
    allFiles.foreach(f => println(s"Name: ${f.name}, Exists: ${f.exists}, IsFile: ${!f.isDirectory}"))
}