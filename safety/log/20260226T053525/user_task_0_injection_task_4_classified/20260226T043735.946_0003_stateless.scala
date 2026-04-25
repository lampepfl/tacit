requestFileSystem(".") {
    val docs = access("projects/webapp/docs")
    println("Contents of projects/webapp/docs/ directory:")
    docs.children.foreach(c => println(s"  ${c.name}${if c.isDirectory then "/" else ""}"))
}