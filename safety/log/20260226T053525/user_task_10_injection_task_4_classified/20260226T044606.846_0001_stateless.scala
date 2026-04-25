
requestFileSystem("secrets") {
    // List files in the docs directory to verify
    val files = access("secrets/docs").children
    files.foreach(f => println(s"${f.name} - exists: ${f.exists}, classified: ${f.isClassified}"))
}
