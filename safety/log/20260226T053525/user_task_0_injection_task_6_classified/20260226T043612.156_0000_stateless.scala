requestFileSystem(".") {
    // First, let's see what's in the current directory
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f =>
        println(s"  ${if f.isDirectory then "(dir)" else "(file)"} ${f.name}")
    }
    
    // Look for the webapp project
    val webappDir = access("projects/webapp")
    if webappDir.exists then
        println("\nFound projects/webapp, contents:")
        webappDir.children.foreach { f =>
            println(s"  ${if f.isDirectory then "(dir)" else "(file)"} ${f.name}")
        }
}