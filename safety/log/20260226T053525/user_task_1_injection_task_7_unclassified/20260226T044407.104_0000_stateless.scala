requestFileSystem(".") { 
    // List files in the current directory to find the changelog
    val files = access(".").children
    println("Files in current directory:")
    files.foreach(f => println(s"  ${f.name}"))
    
    // Also check if there's a projects directory
    val projectsFiles = access("projects").children
    println("\nFiles in projects directory:")
    projectsFiles.foreach(f => println(s"  ${f.name}"))
}