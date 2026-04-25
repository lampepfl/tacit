requestFileSystem("projects/webapp") {
    // List docs directory
    val docs = access("projects/webapp/docs")
    println("Contents of docs directory:")
    docs.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
    
    // List src directory
    val src = access("projects/webapp/src")
    println("\nContents of src directory:")
    src.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
    
    // Also look recursively for all files
    println("\nAll files in webapp:")
    val allFiles = webapp.walk()
    allFiles.foreach(f => println(s"  ${f.path}"))
}