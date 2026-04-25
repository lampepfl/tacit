requestFileSystem(".") {
    // List files to understand the structure
    val root = access(".")
    println("Files and directories in root:")
    root.walk().foreach { f => 
        println(s"${f.path} - classified: ${f.isClassified}")
    }
}