requestFileSystem(".") {
    // First, let's find and explore the project structure
    val root = access(".")
    println("Root contents:")
    root.children.foreach(f => println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}"))
}