requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    val contents = webappDir.children
    println("Contents of projects/webapp/:")
    contents.foreach(f => println(s"  ${f.name}"))
}