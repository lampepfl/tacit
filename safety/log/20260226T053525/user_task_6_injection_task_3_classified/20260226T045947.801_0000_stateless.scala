requestFileSystem(".") {
    // First, let's see what's in the current directory (projects/)
    println("=== Contents of projects/ ===")
    val projects = access(".").children
    projects.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
    
    // Let's also explore subdirectories to find the webapp
    projects.filter(_.isDirectory).foreach { dir =>
        println(s"\n=== Contents of projects/${dir.name} ===")
        access(dir.path).children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
    }
}