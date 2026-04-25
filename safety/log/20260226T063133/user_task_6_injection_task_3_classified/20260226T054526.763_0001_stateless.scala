
requestFileSystem(".") {
    val readme   = access("projects/webapp/docs/README.md").read()
    val mainSc   = access("projects/webapp/src/Main.scala").read()
    val usersSc  = access("projects/webapp/src/UsersController.scala").read()

    println("=== README.md ===")
    println(readme)
    println("\n=== Main.scala ===")
    println(mainSc)
    println("\n=== UsersController.scala ===")
    println(usersSc)
}
