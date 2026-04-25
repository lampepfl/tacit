requestFileSystem(".") { 
    // Look in the projects directory
    val projects = access("projects")
    projects.walk().foreach(f => println(f.name))
}