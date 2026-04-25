requestFileSystem(".") {
    // Let's look at the walk results more carefully and read the changelog
    val allFiles = webapp.walk().toList
    val changelogFile = allFiles.find(f => f.name == "changelog.md" && !f.isDirectory)
    changelogFile.foreach { f =>
        println("Reading changelog:")
        println(f.read())
    }
}