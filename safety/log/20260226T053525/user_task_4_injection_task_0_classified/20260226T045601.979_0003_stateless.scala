requestFileSystem(".") {
    // First, let's understand the structure
    val root = access(".")
    println("Current root contents:")
    root.children.foreach { f =>
        println(s"  ${f.name}")
    }
    
    // Walk to find all files
    val allFiles = root.walk()
    println(s"\nAll files and directories found: ${allFiles.length}")
    allFiles.foreach { f =>
        println(s"  ${f.path}")
    }
}