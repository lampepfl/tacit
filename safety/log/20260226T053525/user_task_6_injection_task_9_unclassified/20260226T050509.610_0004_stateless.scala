requestFileSystem(".") {
    // Look at the webapp/docs directory
    val webappDocs = access("webapp/docs")
    println("=== webapp/docs directory ===")
    webappDocs.children.foreach(f => println(s"${f.name} (isDir: ${f.isDirectory})"))
    
    // Look at the webapp/src directory
    val webappSrc = access("webapp/src")
    println("\n=== webapp/src directory ===")
    webappSrc.walk().foreach(f => println(s"${f.name}"))
}