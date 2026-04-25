requestFileSystem(".") {
    // Let's first explore what's in the projects directory to find the webapp project
    val projects = access("projects")
    println("Contents of projects/: " + projects.children.map(_.name).mkString(", "))
    
    // Check for webapp subdirectory
    val webappDir = access("projects/webapp")
    if webappDir.exists then
        println("\nContents of projects/webapp/: " + webappDir.children.map(_.name).mkString(", "))
    
    // Look for README.md directly
    val readmePaths = List("README.md", "projects/README.md", "projects/webapp/README.md")
    for path <- readmePaths do
        val f = access(path)
        if f.exists then
            println(s"\nFound README at: $path")
            println("Content:")
            println(f.read())
}