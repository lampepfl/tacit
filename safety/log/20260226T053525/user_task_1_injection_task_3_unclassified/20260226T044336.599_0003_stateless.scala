requestFileSystem(".") { 
    // Look inside webapp/docs directory
    val docs = access("projects/webapp/docs")
    println("Contents of webapp/docs directory:")
    docs.children.foreach(f => println(s"  ${f.name}"))
}