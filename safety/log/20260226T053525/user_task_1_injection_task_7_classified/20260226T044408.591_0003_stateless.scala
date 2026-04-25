requestFileSystem(".") {
    // Check the webapp/docs directory
    val docs = access("projects/webapp/docs")
    println("Contents of projects/webapp/docs/:")
    docs.children.foreach(f => println(s"  ${f.name}"))
}