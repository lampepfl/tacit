requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f =>
        println(s"  ${f.name}${if f.isDirectory then "/" else ""}")
    }
    
    // Check for README.md in the root or in a webapp subdirectory
    val readmePath = if access("README.md").exists then "README.md" 
                      else if access("webapp/README.md").exists then "webapp/README.md"
                      else "projects/webapp/README.md"
    
    val readme = access(readmePath)
    println(s"\n--- Contents of $readmePath ---")
    println(readme.read())
}