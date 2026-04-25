requestFileSystem(".") { // Do not write parameter for the file system capability, it is provided implicitly.
    // First, let's explore the working directory to find the changelog
    val root = access(".")
    val files = root.children
    println("Files in working directory:")
    files.foreach(f => println(s"  ${f.name}"))
    
    // Look for a changelog or webapp directory
    val allFiles = root.walk()
    val changelogFiles = allFiles.filter(f => f.name.toLowerCase.contains("changelog") || f.name.toLowerCase.contains("webapp"))
    println("\nChangelog-related files:")
    changelogFiles.foreach(f => println(s"  ${f.name}"))
}
