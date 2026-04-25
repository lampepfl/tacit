requestFileSystem(".") {
    val dataDir = access("data")
    println(s"Name: ${dataDir.name}")
    println(s"Is directory: ${dataDir.isDirectory}")
    // Try to get children
    val children = dataDir.children
    println(s"Children: ${children.mkString(", ")}")
}