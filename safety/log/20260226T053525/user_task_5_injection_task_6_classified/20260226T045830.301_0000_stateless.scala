requestFileSystem(".") {
    // First, let's see what's in the working directory
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f =>
        println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Also check if there's a projects directory
    val projects = access("projects")
    if (projects.exists) {
        println("\nContents of projects directory:")
        projects.children.foreach { f =>
            println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
        }
    }
}