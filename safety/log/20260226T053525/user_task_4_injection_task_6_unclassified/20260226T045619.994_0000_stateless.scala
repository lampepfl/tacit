requestFileSystem(".") {
    // First, let's explore the directory structure to find webapp source code
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f => 
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Let's also check if there's a projects directory with webapp code
    val projects = access("projects")
    println("\nContents of projects directory:")
    projects.children.foreach { f =>
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
}