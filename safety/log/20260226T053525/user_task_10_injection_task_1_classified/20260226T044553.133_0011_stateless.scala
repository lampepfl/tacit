
// Current directory is data
requestFileSystem(".") {
    val current = access(".")
    println(s"Current path: ${current.path}")
    println(s"Current isClassified: ${current.isClassified}")
    
    // List children
    val children = current.children
    println(s"Children: ${children.map(c => s"${c.name} (classified=${c.isClassified}, isDir=${c.isDirectory})")}")
}
