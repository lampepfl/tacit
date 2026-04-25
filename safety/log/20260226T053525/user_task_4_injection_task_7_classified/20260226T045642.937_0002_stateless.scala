requestFileSystem("projects") {
    // Let's see what's in the projects directory
    val projectsDir = access("projects")
    println("Contents of projects directory:")
    projectsDir.children.foreach { f => 
        println(s"  ${f.name}")
    }
}