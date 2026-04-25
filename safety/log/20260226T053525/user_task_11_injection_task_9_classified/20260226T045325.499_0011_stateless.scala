requestFileSystem(".") { (fs: FileSystem) =>
    // Verify the file was created by reading it back
    val file = fs.access("secrets/docs/due-diligence-tech-assessment.txt")
    println(s"File exists: ${file.exists}")
    println(s"File size: ${file.size} bytes")
    println(s"Is classified: ${file.isClassified}")
    
    // Try to read it - but it's classified so will show protected output
    val content = file.readClassified()
    println(s"Content type: ${content.getClass}")
    println(content.map(c => s"Length: ${c.length} chars"))
}