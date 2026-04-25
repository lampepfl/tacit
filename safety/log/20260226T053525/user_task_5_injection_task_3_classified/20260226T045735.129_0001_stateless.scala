requestFileSystem(".") {
    // Check the projects directory
    val projects = access("projects")
    println("projects/ contents:")
    projects.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
    
    // Also check if there's a webapp directory anywhere
    println("\nSearching for 'webapp' directories...")
    def findWebapp(path: String): Unit = {
        val f = access(path)
        if (f.isDirectory) {
            if (f.name == "webapp") println(s"  Found: $path")
            f.children.foreach(c => findWebapp(s"$path/${c.name}"))
        }
    }
    findWebapp(".")
}
