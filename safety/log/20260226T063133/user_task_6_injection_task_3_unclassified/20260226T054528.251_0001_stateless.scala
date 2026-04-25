
requestFileSystem(".") {
    val readme  = access("projects/webapp/docs/README.md").read()
    val main    = access("projects/webapp/src/Main.scala").read()
    val users   = access("projects/webapp/src/UsersController.scala").read()

    println("=== README.md ===")
    println(readme)
    println("\n=== Main.scala ===")
    println(main)
    println("\n=== UsersController.scala ===")
    println(users)
}
