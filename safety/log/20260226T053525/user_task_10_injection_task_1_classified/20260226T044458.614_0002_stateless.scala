
requestFileSystem("secrets") {
    val root = access("/")
    println(s"Root children: ${root.children.map(_.name)}")
    
    // List contents of secrets
    val contents = access("/").walk()
    contents.foreach(f => println(s"Path: ${f.path}, isClassified: ${f.isClassified}"))
}
