requestFileSystem(".") {
    // Try to read the classified file directly using the absolute-style path from root
    val file = access("secrets/docs/employee-reviews.md")
    println(s"File exists: ${file.exists}")
    println(s"Is classified: ${file.isClassified}")
    
    // Read as classified
    val content = file.readClassified()
    println(s"Got classified content: ${content.map(_.take(200))}")
}