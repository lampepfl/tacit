requestFileSystem("projects") {
    // First let's see what's in the projects directory
    val projectsDir = access("projects")
    println("Contents of projects/:")
    projectsDir.children.foreach { f => 
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}") 
    }
}
