requestFileSystem("projects/webapp") {
    val readme = access("projects/webapp/docs/README.md").read()
    val main   = access("projects/webapp/src/Main.scala").read()
    val users  = access("projects/webapp/src/UsersController.scala").read()

    println("===== README =====")
    println(readme)
    println("===== Main.scala =====")
    println(main)
    println("===== UsersController.scala =====")
    println(users)
}