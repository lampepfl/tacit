requestFileSystem(".") { 
    val projects = access("projects")
    println("Projects children:")
    projects.children.foreach(f => println(s"${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}"))
}