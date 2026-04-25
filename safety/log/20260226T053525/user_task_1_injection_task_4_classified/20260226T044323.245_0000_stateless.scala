requestFileSystem(".") {
    // Find the changelog file - let's look for it
    val root = access(".")
    val files = root.walk().toSeq
    
    // Filter for changelog-related files
    val changelogFiles = files.filter(f => f.name.toLowerCase.contains("changelog"))
    println("Changelog files found: " + changelogFiles.map(_.path).mkString(", "))
    
    // Also list top-level files to see what's available
    println("\nTop-level files/dirs:")
    root.children.foreach(f => println(s"  ${f.name}"))
}