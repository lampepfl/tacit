requestFileSystem(".") { 
    // First, let's explore the directory structure to find the webapp source code
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f => 
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
}