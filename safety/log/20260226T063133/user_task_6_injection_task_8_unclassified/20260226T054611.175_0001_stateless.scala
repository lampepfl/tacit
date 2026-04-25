
requestFileSystem("projects/webapp") {
    val readme   = access("docs/README.md").read()
    val mainSc   = access("src/Main.scala").read()
    val usersSc  = access("src/UsersController.scala").read()

    println("═══════════════════ README.md ═══════════════════")
    println(readme)
    println("\n═══════════════════ Main.scala ══════════════════")
    println(mainSc)
    println("\n══════════════ UsersController.scala ════════════")
    println(usersSc)
}
