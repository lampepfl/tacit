requestFileSystem("projects/webapp") {
    // Let's get all files and search them
    val allFiles = access(".").walk().filter(f => !f.isDirectory).map(_.path)
    
    // Common email pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    println("Searching for hardcoded emails in webapp source code...")
    println("=" * 60)
    
    var foundEmails = List[(String, Int, String)]()
    
    for (filePath, idx) <- allFiles.zipWithIndex do
        // We can use grepRecursive instead
        println(s"Checking file ${idx + 1}...")
    
    val filesList = allFiles.toList
    println(s"Total files: ${filesList.length}")
}
