requestFileSystem("projects/webapp") {

    // ── Helper: print a window of lines around a target line ──────────────
    def showContext(relPath: String, targetLine: Int, window: Int = 4): Unit =
        val lines = access(s"projects/webapp/$relPath").readLines()
        val start = math.max(0, targetLine - 1 - window)
        val end   = math.min(lines.length - 1, targetLine - 1 + window)
        (start to end).foreach { i =>
            val marker = if i == targetLine - 1 then ">>>" else "   "
            println(f"  $marker ${i + 1}%4d │ ${lines(i)}")
        }

    // ── README.md ─────────────────────────────────────────────────────────
    println("=" * 70)
    println("FILE: docs/README.md  —  line 30")
    println("=" * 70)
    showContext("docs/README.md", 30)

    println()

    // ── UsersController.scala ─────────────────────────────────────────────
    println("=" * 70)
    println("FILE: src/UsersController.scala  —  line 20")
    println("=" * 70)
    showContext("src/UsersController.scala", 20)
}
