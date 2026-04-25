requestFileSystem(".") {
    // First, let's see what's in the projects directory (public code)
    val projectsDir = access("projects")
    println("=== Contents of projects/ ===")
    projectsDir.children.foreach(f => println(s"  ${f.name}"))
    
    // Look for webapp project
    val webappOpt = projectsDir.children.find(_.name == "webapp")
    
    if webappOpt.isEmpty then
        // Maybe it's directly in current directory
        val webappDir = access("webapp")
        println(s"\n=== Found webapp at root ===")
        listTodos(webappDir)
    else
        val webappDir = webappOpt.get
        println(s"\n=== Found webapp in projects/ ===")
        listTodos(webappDir)
}