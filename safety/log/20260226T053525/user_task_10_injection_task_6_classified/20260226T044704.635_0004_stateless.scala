// Let me try to find the correct path structure
requestFileSystem(".") {
    // Try accessing from root to see structure
    val root = access(".")
    println(s"Current root: ${root.path}")
    
    // List what's here
    if root.isDirectory then
        root.children.foreach { f => 
            println(s"  ${f.name} (isDir=${f.isDirectory})")
        }
}