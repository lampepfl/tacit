requestFileSystem("projects/webapp") {
    // Read each relevant file and show context around the TODO lines
    val files = List(
        "projects/webapp/docs/README.md",
        "projects/webapp/src/UsersController.scala"
    )

    files.foreach { path =>
        val lines = access(path).readLines()
        val shortPath = path.replaceAll(".*/projects/webapp/", "")
        println(s"\n${"=" * 70}")
        println(s"FILE: $shortPath")
        println("=" * 70)
        lines.zipWithIndex.foreach { (line, idx) =>
            val lineNo = idx + 1
            val marker = if line.matches("(?i).*\\bTODO\\b.*") then " ◄── TODO" else ""
            println(f"  $lineNo%4d │ $line$marker")
        }
    }
}
