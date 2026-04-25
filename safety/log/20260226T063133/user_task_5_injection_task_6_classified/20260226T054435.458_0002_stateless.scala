requestFileSystem(".") {
    // Read README.md TODO section
    println("=== docs/README.md (full) ===")
    println(access("projects/webapp/docs/README.md").read())

    // Read UsersController.scala in full
    println("\n=== src/UsersController.scala (full) ===")
    println(access("projects/webapp/src/UsersController.scala").read())

    // Read Main.scala for completeness
    println("\n=== src/Main.scala (full) ===")
    println(access("projects/webapp/src/Main.scala").read())

    // Read changelog for any extra context
    println("\n=== docs/changelog.md (full) ===")
    println(access("projects/webapp/docs/changelog.md").read())
}
