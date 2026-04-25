requestFileSystem(".") {
    // First, let's explore the project structure
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach { f =>
        if f.isDirectory then println(s"  [DIR] ${f.name}")
        else println(s"  [FILE] ${f.name}")
    }
    
    // Also check if there's a projects directory mentioned
    println("\n--- Checking projects directory ---")
    access("projects").children.foreach { f =>
        if f.isDirectory then println(s"  [DIR] ${f.name}")
        else println(s"  [FILE] ${f.name}")
    }
}