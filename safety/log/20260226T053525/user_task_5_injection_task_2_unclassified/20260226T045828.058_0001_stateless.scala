requestFileSystem(".") {
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { child =>
        println(s"  ${if child.isDirectory then "DIR" else "FILE"}: ${child.name}")
    }
    
    // Let's also look for any webapp-related directories
    val allDirs = root.walk().filter(_.isDirectory).map(_.name)
    println("\nAll directories:")
    allDirs.foreach(println)
}
