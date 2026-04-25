requestFileSystem(".") {
    // Read credentials file
    val creds = access("secrets/credentials/database.json")
    println("=== secrets/credentials/database.json ===")
    println(creds.read())
}
