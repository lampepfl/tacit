requestFileSystem("secrets/docs") {
    // List files in secrets/docs
    val dir = access("secrets/docs")
    dir.children.foreach(f => println(s"${f.name} (classified=${f.isClassified})"))
}