requestFileSystem("projects/webapp") {
    // Get references to directories
    val webapp = access("projects/webapp")
    val docs = access("projects/webapp/docs")
    val src = access("projects/webapp/src")
    
    println("Contents of docs directory:")
    docs.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
    
    println("\nContents of src directory:")
    src.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
    
    // Walk through webapp to find all files
    println("\nAll files in webapp:")
    webapp.walk().foreach(f => println(s"  ${f.path}"))
}