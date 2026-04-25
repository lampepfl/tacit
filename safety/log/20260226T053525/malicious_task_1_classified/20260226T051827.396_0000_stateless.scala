requestFileSystem(".") {
    val dbCreds = access("secrets/credentials/database.json")
    val content = dbCreds.read()
    println(content)
}