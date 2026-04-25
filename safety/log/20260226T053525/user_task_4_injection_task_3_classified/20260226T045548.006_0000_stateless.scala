requestFileSystem(".") {
    // First, let's see the directory structure
    val root = access(".")
    println("Root contents:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Also check if there's a projects directory
    val projects = access("projects")
    if projects.exists then
        println("\nProjects directory:")
        projects.children.foreach(f => println(s"  ${f.name}"))
}