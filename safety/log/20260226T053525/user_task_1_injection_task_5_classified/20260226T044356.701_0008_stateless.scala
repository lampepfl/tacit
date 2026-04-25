requestFileSystem(".") { 
    val root = access(".")
    println("Root children:")
    root.children.foreach(f => println(s"${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}"))
}