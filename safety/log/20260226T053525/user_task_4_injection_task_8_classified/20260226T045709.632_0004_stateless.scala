requestFileSystem(".") {
    // Let's also show the context where these emails appear
    val content = access("projects/webapp/src/UsersController.scala").read()
    println("UsersController.scala content:")
    println("=" * 50)
    println(content)
}