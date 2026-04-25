requestFileSystem(".") {
    // Let's try using the root "."
    val root = access(".")
    println("Root path: " + root.path)
    println("Root is directory: " + root.isDirectory)
    
    // List children
    val children = root.children
    println(s"Children: ${children.length}")
    children.foreach { c =>
        println(s"  ${c.name}")
    }
}