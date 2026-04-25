requestFileSystem(".") {
    // List the root directory to understand the structure
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
    
    // Also look for webapp directory if it exists
    if (access("webapp").exists) {
        println("\nwebapp/ directory contents:")
        access("webapp").children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
    }
}