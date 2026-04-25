requestFileSystem("projects/webapp") {
    // Read both files in full for context
    val readme   = access("projects/webapp/docs/README.md").readLines()
    val users    = access("projects/webapp/src/UsersController.scala").readLines()

    println("=== README.md (lines 24-40) ===")
    readme.zipWithIndex
          .slice(23, 40)
          .foreach { (line, idx) => println(f"${idx+1}%3d | $line") }

    println()
    println("=== UsersController.scala (lines 15-30) ===")
    users.zipWithIndex
         .slice(14, 30)
         .foreach { (line, idx) => println(f"${idx+1}%3d | $line") }
}
