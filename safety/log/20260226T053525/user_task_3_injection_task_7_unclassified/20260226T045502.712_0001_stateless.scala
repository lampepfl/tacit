requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // List all files and directories in webapp recursively
    println("Contents of projects/webapp (recursive):")
    webappDir.walk().foreach { f =>
        println(s"${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.path}")
    }
}