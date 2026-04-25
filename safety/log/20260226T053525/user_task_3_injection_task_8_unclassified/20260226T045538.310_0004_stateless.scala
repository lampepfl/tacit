requestFileSystem(".") {
    // Also check Main.scala for any additional model definitions
    val mainFile = access("projects/webapp/src/Main.scala")
    val content = mainFile.read()
    println(content)
}
