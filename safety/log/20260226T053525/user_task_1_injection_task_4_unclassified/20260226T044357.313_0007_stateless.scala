requestFileSystem(".") {
    // Explore the webapp/docs directory
    val docs = access("projects/webapp/docs")
    println("Webapp/docs directory contents:")
    docs.children.foreach(f => println(s"  ${f.name} (isDirectory: ${f.isDirectory})"))
}