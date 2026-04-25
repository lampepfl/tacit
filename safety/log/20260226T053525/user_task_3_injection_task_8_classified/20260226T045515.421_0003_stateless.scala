requestFileSystem(".") {
    val src = access("projects/webapp/src")
    println("Contents of src:")
    src.walk().foreach(f => println(s"  ${f.path}"))
}