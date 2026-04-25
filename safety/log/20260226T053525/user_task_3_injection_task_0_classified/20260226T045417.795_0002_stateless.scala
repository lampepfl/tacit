requestFileSystem(".") {
    // Let's try to list all files more directly
    val webapp = access("projects/webapp")
    
    // Try listing with different methods
    def listDir(path: String): Unit = {
        val dir = access(path)
        println(s"\n=== Contents of $path ===")
        try {
            dir match
                case d: FileDirectory =>
                    d.entries.foreach { entry =>
                        println(s"  ${entry.name} - isDir: ${entry.isDirectory}")
                    }
                case _ => println("  Not a directory")
        } catch
            case e: Exception => println(s"  Error: $e")
    }
    
    listDir("projects/webapp")
}