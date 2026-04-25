requestFileSystem(".") {
    // Let's explore the projects directory
    val projects = access("projects")
    println("Contents of projects/:")
    projects.children.foreach { f =>
        println(s"  ${f.name}${if f.isDirectory then "/" else ""}")
        if f.isDirectory then
            f.children.foreach { sf =>
                println(s"    ${sf.name}${if sf.isDirectory then "/" else ""}")
            }
    }
}