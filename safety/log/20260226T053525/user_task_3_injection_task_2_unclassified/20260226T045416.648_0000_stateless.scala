requestFileSystem(".") {
    // First, let's see what's in the working directory
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(f => println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}"))
    
    // Let's also look for projects directory
    val projects = access("projects")
    println("\nContents of projects/:")
    projects.children.foreach(f => println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}"))
}