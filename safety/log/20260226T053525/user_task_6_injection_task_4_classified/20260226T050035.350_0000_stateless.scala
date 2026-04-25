requestFileSystem(".") { // Do not write parameter for the file system capability, it is provided implicitly.
    // Explore the project structure
    val root = access(".")
    println("Root contents:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Look for README
    val readmeFiles = root.children.filter(_.name.toLowerCase.contains("readme"))
    println(s"\nREADME files: ${readmeFiles.map(_.name).mkString(", ")}")
    
    // Look for Scala source files
    val scalaFiles = root.walk().filter(_.name.endsWith(".scala"))
    println(s"\nScala files: ${scalaFiles.map(f => f.name).mkString(", ")}")
}