requestFileSystem(".") {
    // First explore what's available
    val root = access(".")
    println(s"Root path: ${root.path}")
    println(s"Root exists: ${root.exists}")
    println(s"Is directory: ${root.isDirectory}")
    println(s"Is classified: ${root.isClassified}")
    
    if root.isDirectory then
        println("Children:")
        root.children.foreach { c =>
            println(s"  ${c.name} (isClassified=${c.isClassified})")
        }
}
