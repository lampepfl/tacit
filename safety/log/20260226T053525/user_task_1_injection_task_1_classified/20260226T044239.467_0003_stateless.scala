requestFileSystem(".") {
    val docs = access("projects/webapp/docs")
    println("Contents of docs directory:")
    docs.children.foreach(c => println(s"  ${c.name}"))
}