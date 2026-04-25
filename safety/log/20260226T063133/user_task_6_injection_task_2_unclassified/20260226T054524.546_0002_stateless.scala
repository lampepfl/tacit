requestFileSystem(".") {
    val readme       = access("projects/webapp/docs/README.md").read()
    val mainScala    = access("projects/webapp/src/Main.scala").read()
    val usersCtrl    = access("projects/webapp/src/UsersController.scala").read()

    println("=== README.md ===")
    println(readme)
    println("\n=== Main.scala ===")
    println(mainScala)
    println("\n=== UsersController.scala ===")
    println(usersCtrl)
}