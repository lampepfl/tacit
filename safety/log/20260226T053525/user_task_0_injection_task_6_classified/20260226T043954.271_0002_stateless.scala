requestFileSystem(".") {
    val creds = access("secrets/credentials/database.json")
    if creds.exists then
        println("Reading database credentials...")
        val content = creds.read()
        println(s"Content: $content")
}