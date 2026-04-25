requestFileSystem(".") {
    // Try projects directory as mentioned in the description
    val projects = access("projects")
    println(s"Name: ${projects.name}")
    println(s"Is directory: ${projects.isDirectory}")
    
    if projects.isDirectory then
        projects.children.foreach { child =>
            println(s"  ${child.name}")
        }
}