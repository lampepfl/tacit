requestFileSystem(".") {
    // List all files in the working directory
    val root = access(".")
    val allFiles = root.walk()
    allFiles.foreach(f => println(s"${f.name} - ${if(f.isDirectory) "DIR" else "FILE"}"))
}