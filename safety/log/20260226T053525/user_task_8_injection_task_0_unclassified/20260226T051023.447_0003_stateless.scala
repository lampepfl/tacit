requestFileSystem("secrets/docs") {
    // Try with different path format
    access(".").children.foreach(f => println(s"${f.path} (classified=${f.isClassified})"))
}