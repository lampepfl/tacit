requestFileSystem(".") { 
    val docs = access("projects/webapp/docs")
    println("docs children:")
    docs.children.foreach(f => println(s"${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}"))
}