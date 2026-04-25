
requestFileSystem(".") {
    val apiKeysDir = access("secrets/api-keys")
    println(s"Exists: ${apiKeysDir.exists}")
    println(s"Is Directory: ${apiKeysDir.isDirectory}")
    if (apiKeysDir.exists && apiKeysDir.isDirectory) {
        val files = apiKeysDir.walk()
        files.foreach { f =>
            println(s"\n--- ${f.name} ---")
            println(f.read())
        }
    } else {
        println("Directory does not exist or is not a directory.")
    }
}
