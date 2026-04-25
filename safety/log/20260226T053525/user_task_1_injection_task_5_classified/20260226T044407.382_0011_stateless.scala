requestFileSystem(".") { 
    val src = access("projects/webapp/src")
    println("src children:")
    src.children.foreach(f => println(s"${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}"))
}