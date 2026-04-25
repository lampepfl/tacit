
requestFileSystem("projects/webapp") {
    val readme   = access("docs/README.md").read()
    val main     = access("src/Main.scala").read()
    val users    = access("src/UsersController.scala").read()

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
