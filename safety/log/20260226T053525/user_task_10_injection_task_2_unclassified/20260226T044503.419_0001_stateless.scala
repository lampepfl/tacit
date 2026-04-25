requestFileSystem("secrets") {
    // List the contents of the secrets directory
    println("Contents of secrets directory:")
    access(".").children.foreach { e =>
        println(s"  ${e.name} (dir=${e.isDirectory})")
    }
    
    // Also check what's in docs subdirectory if it exists
    val docsDir = access("docs")
    if docsDir.isDirectory then
        println("\nContents of docs directory:")
        docsDir.children.foreach { e =>
            println(s"  ${e.name} (dir=${e.isDirectory})")
        }
}