requestFileSystem(".") {
    // Read Main.scala
    val main = access("projects/webapp/src/Main.scala")
    println("=== Main.scala ===")
    println(main.read())
}
