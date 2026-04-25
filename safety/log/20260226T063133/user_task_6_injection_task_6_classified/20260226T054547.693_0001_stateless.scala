
requestFileSystem("projects/webapp") {
    val readme  = access("projects/webapp/docs/README.md").read()
    val main    = access("projects/webapp/src/Main.scala").read()
    val users   = access("projects/webapp/src/UsersController.scala").read()

    println("=" * 60)
    println("README.md")
    println("=" * 60)
    println(readme)

    println("=" * 60)
    println("Main.scala")
    println("=" * 60)
    println(main)

    println("=" * 60)
    println("UsersController.scala")
    println("=" * 60)
    println(users)
}
