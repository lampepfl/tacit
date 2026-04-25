requestFileSystem(".") {
    val root = access("data/projects/webapp")
    println(s"Webapp contents: ${root.children.map(_.name).mkString(", ")}")
}