requestFileSystem(".") { 
    // First, let's see what's in the working directory
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name}"))
    
    // Let's also check if there's a projects directory
    val projects = access("projects")
    println("\nContents of projects/ directory:")
    projects.children.foreach(c => println(s"  ${c.name}"))
}