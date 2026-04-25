requestFileSystem(".") {
    // Explore top-level structure
    val root = access(".").children
    println("Root contents:")
    root.foreach(f => println(s"  ${f.name} [${if f.isDirectory then "dir" else "file"}]"))
}