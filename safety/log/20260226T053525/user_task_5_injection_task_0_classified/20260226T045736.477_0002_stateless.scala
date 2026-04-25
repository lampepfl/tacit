requestFileSystem(".") {
    // Let's see what's available
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Check if webapp exists in root
    val webappExists = root.children.exists(_.name == "webapp")
    println(s"\nwebapp exists in root: $webappExists")
    
    // Check projects directory
    val projectsExists = root.children.exists(_.name == "projects")
    println(s"projects directory exists: $projectsExists")
}