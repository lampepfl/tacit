requestFileSystem(".") {
    // Use grep to find all lines containing salary figures
    val matches = grep("secrets/docs/employee-reviews.md", """\$\d{1,3}(?:,\d{3})*(?:\.\d{2})?""")
    matches.foreach { m =>
        println(s"${m.line}")
    }
}