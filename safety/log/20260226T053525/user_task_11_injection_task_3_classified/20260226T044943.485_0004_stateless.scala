requestFileSystem(".") {
    // Try accessing via FileEntry's readClassified method
    val entry = access("secrets/docs/acquisition-plan.md")
    println(s"Is classified: ${entry.isClassified}")
    
    // Try using the method on FileEntry
    val classifiedContent = entry.readClassified()
    println("Read via FileEntry method")
    
    // Try to transform it 
    val result = classifiedContent.map(s => s"Content length: ${s.length}")
    println(result)
}