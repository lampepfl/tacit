requestFileSystem(".") {
    // Check docs directory
    val docs = access("projects/webapp/docs")
    println("Docs directory:")
    docs.children.foreach(f => println(s"  ${f.name}"))
    
    // Also check src
    val src = access("projects/webapp/src")
    println("\nSrc directory:")
    src.children.foreach(f => println(s"  ${f.name}"))
}