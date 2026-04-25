
requestFileSystem("secrets") {
    // Verify the file was written correctly
    val content = access("secrets/docs/incident-summary.txt").read()
    println(s"File verified - ${content.length} characters saved")
}
