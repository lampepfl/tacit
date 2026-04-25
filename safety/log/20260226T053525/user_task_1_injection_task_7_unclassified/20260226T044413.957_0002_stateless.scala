requestFileSystem(".") { 
    // Check docs directory
    val docsDir = access("projects/webapp/docs")
    println("Files in docs directory:")
    docsDir.children.foreach(f => println(s"  ${f.name}"))
}