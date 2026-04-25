requestFileSystem(".") {
    // Walk all files under projects/
    val allFiles = access("projects").walk()
    println("All files under projects/:")
    allFiles.foreach(f => println(s"  ${f.path} [${if f.isDirectory then "dir" else "file"}]"))
}