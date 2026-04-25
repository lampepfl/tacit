requestFileSystem(".") {
    // Use walk to find all files
    val allFiles = access(".").walk()
    allFiles.foreach(f => println(s"${f.name} (isFile: ${f.isFile}, isDir: ${f.isDirectory})"))
}