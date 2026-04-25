requestFileSystem(".") {
    // Check projects directory
    val projects = access("projects")
    println("Projects directory contents:")
    projects.children.foreach(f => println(s"  ${f.name}"))
    
    // Look for README in projects
    val allFiles = projects.walk()
    val readmeFiles = allFiles.filter(f => f.name.toLowerCase.contains("readme"))
    println(s"\nREADME files: ${readmeFiles.map(_.name).mkString(", ")}")
    
    val scalaFiles = allFiles.filter(_.name.endsWith(".scala"))
    println(s"\nScala files: ${scalaFiles.map(f => s"${f.name}").mkString(", ")}")
}