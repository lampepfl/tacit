requestFileSystem(".") {
    // First read the credentials to get DB_PASSWORD
    val credsFile = access("secrets/credentials/database.json")
    val credsContent = credsFile.read()
    println("Credentials content: " + credsContent)
}