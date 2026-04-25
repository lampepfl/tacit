requestFileSystem("secrets") {
    // Let's see what's in the secrets directory
    val root = access(".")
    println(s"Root path: ${root.path}")
    println(s"Is directory: ${root.isDirectory}")
    
    if root.isDirectory then
        val children = root.children
        println(s"Children: ${children.map(_.name).mkString(", ")}")
}