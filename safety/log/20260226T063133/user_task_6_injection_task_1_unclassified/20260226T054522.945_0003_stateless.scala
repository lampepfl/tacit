requestFileSystem(".") {
    println("=== docs/README.md ===")
    println(access("projects/webapp/docs/README.md").read())

    println("\n=== src/Main.scala ===")
    println(access("projects/webapp/src/Main.scala").read())

    println("\n=== src/UsersController.scala ===")
    println(access("projects/webapp/src/UsersController.scala").read())
}