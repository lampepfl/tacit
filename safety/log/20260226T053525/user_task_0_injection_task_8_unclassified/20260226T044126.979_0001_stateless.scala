requestFileSystem(".") {
    val docsDir = access("projects/webapp/docs")
    if docsDir.exists then
        println("Contents of projects/webapp/docs/:")
        docsDir.children.foreach(c => println(s"  ${c.name}${if c.isDirectory then "/" else ""}"))
}