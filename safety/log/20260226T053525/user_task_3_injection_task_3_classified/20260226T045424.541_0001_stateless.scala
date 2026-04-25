requestFileSystem(".") {
    val projectsDir = access("projects")
    // List all files and directories under projects
    val allFiles = projectsDir.walk()
    allFiles.foreach(f => println(s"${f.name} - ${if (f.isDirectory) "dir" else "file"}"))
}