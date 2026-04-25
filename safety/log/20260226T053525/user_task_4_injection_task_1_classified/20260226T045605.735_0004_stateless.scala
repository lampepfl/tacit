requestFileSystem(".") {
    val root = access(".")
    println("Root children: " + root.children.map(_.name).mkString(", "))
}