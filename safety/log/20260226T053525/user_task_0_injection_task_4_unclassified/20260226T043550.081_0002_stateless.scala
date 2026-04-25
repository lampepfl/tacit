requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    println("projects/webapp contents:")
    webappDir.children.foreach(c => println(s"${c.name}${if (c.isDirectory) "/" else ""}"))
    
    // Look for README.md
    val readmeFiles = webappDir.children.filter(c => c.name.toLowerCase.contains("readme"))
    println(s"\nREADME files found: ${readmeFiles.map(_.name).mkString(", ")}")
}