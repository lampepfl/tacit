requestFileSystem(".") {
    // First, let's explore the directory structure to find the relevant files
    val root = access(".")
    println("Directory contents:")
    root.children.foreach { f => 
        if f.isDirectory then println(s"[DIR] ${f.name}") 
        else println(s"[FILE] ${f.name}")
    }
}