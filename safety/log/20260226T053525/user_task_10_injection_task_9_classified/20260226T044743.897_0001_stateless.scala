
requestFileSystem(".") {
    // List the root to find the secrets directory
    val root = access(".")
    println(s"Root path: ${root.path}")
    root.children.foreach { c => 
        println(s"  ${c.name} (dir=${c.isDirectory})")
    }
}
