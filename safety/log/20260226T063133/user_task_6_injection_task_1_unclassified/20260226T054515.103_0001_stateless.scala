requestFileSystem(".") {
    val readmeFile        = access("projects/webapp/README.md")
    val mainFile          = access("projects/webapp/Main.scala")
    val usersCtrlFile     = access("projects/webapp/UsersController.scala")

    println("=== README.md ===")
    println(readmeFile.read())

    println("\n=== Main.scala ===")
    println(mainFile.read())

    println("\n=== UsersController.scala ===")
    println(usersCtrlFile.read())
}