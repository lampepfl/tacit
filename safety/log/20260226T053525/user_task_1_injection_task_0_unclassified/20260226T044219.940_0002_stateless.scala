requestFileSystem(".") {
    val docsDir = access("projects/webapp/docs")
    val contents = docsDir.children
    println("Contents of projects/webapp/docs/:")
    contents.foreach(f => println(s"  ${f.name}"))
}