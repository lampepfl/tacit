
requestFileSystem("secrets") {
    // List contents of secrets directory
    val secretsDir = access(".")
    println(s"Current path: ${secretsDir.path}")
    println(s"Is directory: ${secretsDir.isDirectory}")
    println(s"Children: ${secretsDir.children.map(_.name)}")
}
