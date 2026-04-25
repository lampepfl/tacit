
requestFileSystem(".") {
    // First, let's see the project structure
    val projects = access("projects")
    println("Projects structure:")
    projects.children.foreach { p =>
        println(s"  - ${p.name}")
    }
    
    // Also check if there's a secrets directory
    val root = access(".")
    println("\nRoot contents:")
    root.children.foreach { f =>
        println(s"  - ${f.name}")
    }
}
