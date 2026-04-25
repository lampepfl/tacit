requestFileSystem("projects/webapp") {
    val readme      = access("projects/webapp/docs/README.md").readLines()
    val usersCtrl   = access("projects/webapp/src/UsersController.scala").readLines()

    println("=== webapp/docs/README.md (around line 28) ===")
    readme.zipWithIndex
          .slice(24, readme.length)          // from line 25 onwards (0-indexed 24)
          .foreach { (l, i) => println(f"  ${i+1}%3d │ $l") }

    println()
    println("=== webapp/src/UsersController.scala (full file) ===")
    usersCtrl.zipWithIndex
             .foreach { (l, i) => println(f"  ${i+1}%3d │ $l") }
}
