requestFileSystem(".") {
    // Let's see what the working directory actually is
    val root = access(".")
    println(s"Current path: ${root.path}")
    println(s"Children: ${root.children.map(_.name)}")
    
    // Try to find files in a relative way
    val searchResults = grepRecursive("projects", "README", "*")
    println(s"Grep results: $searchResults")
}