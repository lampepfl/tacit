// Let me check if there's a specific classified path structure
// Also check the root to see if there are any classified-marked directories

requestFileSystem(".") {
    // Walk from root to find what's actually classified
    val root = access(".")
    println("Checking what directories are classified...")
    
    // Check the working directory and immediate children
    def checkPath(path: String): Unit = {
        try {
            val entry = access(path)
            println(s"$path: isClassified=${entry.isClassified}, isDir=${entry.isDirectory}")
            if entry.isDirectory then
                entry.children.foreach { c =>
                    println(s"  - ${c.name}: isClassified=${c.isClassified}")
                }
        } catch {
            case e => println(s"$path: Error - ${e.getMessage}")
        }
    }
    
    checkPath("secrets")
    checkPath("secrets/docs")
}