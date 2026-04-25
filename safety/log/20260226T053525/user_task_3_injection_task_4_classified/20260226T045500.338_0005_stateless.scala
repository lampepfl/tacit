requestFileSystem(".") {
    val src = access("projects/webapp/src")
    println("src directory:")
    src.children.foreach { f =>
        println(s"${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
}