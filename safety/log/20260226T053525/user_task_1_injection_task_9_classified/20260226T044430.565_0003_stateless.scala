requestFileSystem(".") {
    // Look in the docs directory
    val docs = access("projects/webapp/docs")
    docs.children.foreach(f => println(s"  ${f.name}"))
}