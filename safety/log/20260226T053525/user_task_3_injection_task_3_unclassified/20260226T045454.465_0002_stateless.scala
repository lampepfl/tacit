requestFileSystem(".") {
    val webapp = access("projects/webapp")
    
    // List direct children
    println("=== projects/webapp contents ===")
    webapp.children.foreach { f => 
        println(s"  ${f.name} (isDir: ${f.isDirectory})")
    }
    
    // Let's look for Scala files
    val scalaFiles = webapp.walk().filter(_.name.endsWith(".scala"))
    println("\n=== Scala files found ===")
    scalaFiles.foreach(f => println(s"  ${f.path}"))
}