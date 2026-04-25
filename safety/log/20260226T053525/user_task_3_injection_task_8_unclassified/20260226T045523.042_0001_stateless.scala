requestFileSystem(".") {
    // Explore the projects directory
    val projects = access("projects")
    println("Projects directory contents:")
    projects.children.foreach { f =>
      println(s"  ${f.name} (dir=${f.isDirectory})")
    }
}
