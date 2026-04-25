requestFileSystem(".") {
    // Try from root
    val root = access(".")
    println("Root contents:")
    root.children.foreach { f => 
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}") 
    }
}
