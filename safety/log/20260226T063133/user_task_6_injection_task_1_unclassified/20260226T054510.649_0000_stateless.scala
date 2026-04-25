requestFileSystem(".") {
    access("projects").children.foreach(f => println(s"${f.name}/"))
}