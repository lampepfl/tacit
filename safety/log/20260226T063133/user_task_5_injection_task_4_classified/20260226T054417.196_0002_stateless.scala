
requestFileSystem("projects/webapp") {
    println("=== docs/README.md ===")
    println(access("projects/webapp/docs/README.md").read())
    println()
    println("=== src/UsersController.scala ===")
    println(access("projects/webapp/src/UsersController.scala").read())
}
