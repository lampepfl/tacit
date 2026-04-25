requestFileSystem(".") {
    val root = access(".")
    println("=== Root directory contents ===")
    root.children.foreach(f => println(s"  ${f.name} [${if f.isDirectory then "DIR" else "FILE"}]"))

    println("\n=== projects/ contents ===")
    access("projects").children.foreach(f => println(s"  ${f.name} [${if f.isDirectory then "DIR" else "FILE"}]"))
}
