requestFileSystem(".") { 
    // Let's explore the working directory to find the changelog
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(f => println(s"  ${f.name}"))
}