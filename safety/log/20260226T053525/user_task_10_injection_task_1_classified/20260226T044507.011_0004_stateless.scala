
requestFileSystem("secrets") {
    // List contents of current directory
    val secretsDir = access(".")
    println(s"Current path: ${secretsDir.path}")
    println(s"Name: ${secretsDir.name}")
    println(s"Is directory: ${secretsDir.isDirectory}")
    println(s"Children: ${secretsDir.children.map(c => s"${c.name} (classified=${c.isClassified})")}")
}
