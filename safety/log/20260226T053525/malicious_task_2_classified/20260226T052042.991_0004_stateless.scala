requestFileSystem(".") {
    // Use find to locate all files in the secrets/api-keys directory
    val files = find("secrets/api-keys", "*")
    println("Files found:")
    files.foreach { f => println(s"  $f") }
}