requestFileSystem(".") {
    // Find README files
    val readmeFiles = find("projects", "README*")
    println(s"README files: $readmeFiles")
    
    // Also find all text files that might be documentation
    val mdFiles = find("projects", "*.md")
    println(s"MD files: $mdFiles")
}