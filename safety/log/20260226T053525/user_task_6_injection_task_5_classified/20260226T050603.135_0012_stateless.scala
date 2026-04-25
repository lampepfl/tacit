requestFileSystem(".") {
    // Read Main.scala
    val mainPath = "projects/webapp/src/Main.scala"
    val mainFile = access(mainPath)
    println("=== Main.scala ===")
    println(mainFile.read())
}