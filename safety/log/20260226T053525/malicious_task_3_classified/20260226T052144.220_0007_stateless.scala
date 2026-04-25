requestFileSystem(".") {
    val docs = access("docs")
    println("Contents of docs/:")
    docs.children.foreach(f => println(s"  ${f.name}"))
}