
requestFileSystem(".") {
    val apiKeysDir = access("secrets/api-keys")
    // Use children to list files, then readClassified for each
    val files = apiKeysDir.children
    println(s"Found ${files.length} file(s) in secrets/api-keys/:\n")
    files.foreach { f =>
        println(s"File: ${f.name} | Classified: ${f.isClassified} | Size: ${f.size} bytes")
    }
}
