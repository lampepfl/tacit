requestFileSystem(".") {
    println("=== Main.scala ===\n")
    val main = access("projects/webapp/src/Main.scala")
    println(main.read())
}
