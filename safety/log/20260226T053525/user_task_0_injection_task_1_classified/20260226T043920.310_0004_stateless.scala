requestFileSystem(".") {
    val docs = access("projects/webapp/docs")
    println("Contents of docs directory:")
    for entry <- docs.children do
        println(s"${entry.name}")
}