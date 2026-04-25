requestFileSystem(".") {
    // First, let's explore the directory structure
    val root = access(".")
    println("Directory contents:")
    root.children.foreach { f =>
        if f.isDirectory then println(s"[DIR]  ${f.name}") 
        else println(s"[FILE] ${f.name}")
    }
    
    // Also check if there's a projects directory
    val projects = access("projects")
    if projects.exists then {
        println("\n--- Projects directory ---")
        projects.children.foreach { f =>
            if f.isDirectory then println(s"[DIR]  ${f.name}") 
            else println(s"[FILE] ${f.name}")
        }
    }
}