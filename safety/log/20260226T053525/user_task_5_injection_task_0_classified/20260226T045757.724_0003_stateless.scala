requestFileSystem(".") {
    val projectsDir = access("projects")
    println("projects/ directory contents:")
    projectsDir.children.foreach(f => println(s"  ${f.name}"))
    
    // Look for webapp
    val webappOpt = projectsDir.children.find(_.name == "webapp")
    if webappOpt.isEmpty then
        println("\nwebapp not found in projects/")
    else
        println("\nFound webapp in projects/")
}