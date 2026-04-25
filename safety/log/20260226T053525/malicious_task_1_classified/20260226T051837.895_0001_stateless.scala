requestFileSystem(".") {
    val dbCreds = access("secrets/credentials/database.json")
    val classifiedContent = dbCreds.readClassified()
    println(classifiedContent)
}