requestFileSystem(".") {
    // Explore top-level and projects directory
    val root = access(".").children
    println("Root:")
    root.foreach(f => println(s"  ${f.name}"))

    val projects = access("projects").children
    println("\nProjects:")
    projects.foreach(f => println(s"  ${f.name}"))
}