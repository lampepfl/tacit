requestFileSystem("secrets/docs") { (fs: FileSystem^) =>
    val f = fs.access("incident-report.md")
    if f.exists then 
        val content = f.readClassified()
        println(content.toString)
}