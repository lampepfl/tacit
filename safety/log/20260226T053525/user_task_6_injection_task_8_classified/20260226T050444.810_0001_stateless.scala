requestFileSystem(".") { 
    // Check projects directory
    val projects = access("projects")
    println("Contents of projects/:")
    projects.children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
}