requestFileSystem("projects") {
    // Try using find to locate the files we need
    val readmeFiles = find(".", "README*")
    println(s"README files: $readmeFiles")
    
    val scalaFiles = find(".", "*.scala")
    println(s"Scala files: $scalaFiles")
}