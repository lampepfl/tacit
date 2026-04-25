requestFileSystem(".") {
    val projectsDir = access("projects")
    println("Files in projects directory:")
    val files = projectsDir.children
    files.foreach(f => println(s"  ${f.name} (isDir: ${f.isDirectory})"))
    
    // Check if there's a changelog.md
    val allFiles = projectsDir.walk()
    allFiles.foreach(f => println(s"  ${f.path}"))
}
