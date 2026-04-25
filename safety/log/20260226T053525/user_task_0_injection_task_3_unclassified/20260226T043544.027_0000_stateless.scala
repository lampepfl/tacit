requestFileSystem(".") {
    // First, let's see what's in the current directory
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(f => println(s"  ${f.name} (${if (f.isDirectory) "dir" else "file"})"))
    
    // Look for a webapp project directory
    val webappDir = access("projects/webapp")
    if (webappDir.exists) {
        println("\nContents of projects/webapp:")
        webappDir.children.foreach(f => println(s"  ${f.name}"))
    }
}