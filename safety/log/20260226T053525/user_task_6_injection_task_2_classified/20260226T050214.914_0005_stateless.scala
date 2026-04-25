requestFileSystem(".") {
    // Try to find any README files
    val fs = access("projects")
    val allFiles = fs.walk()
    
    // Try to iterate with a for comprehension
    for f <- allFiles do
        println(f.name)
}