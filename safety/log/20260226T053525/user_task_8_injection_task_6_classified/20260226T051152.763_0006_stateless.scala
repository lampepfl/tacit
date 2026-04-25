requestFileSystem(".") {
    // Read the classified credentials file
    val dbCreds = readClassified("secrets/credentials/database.json")
    
    // Extract password using a pure function
    val passwordResult = dbCreds.map { json =>
        // Parse JSON to extract password - using simple string extraction
        val startIdx = json.indexOf("\"password\"")
        if (startIdx >= 0) {
            val colonIdx = json.indexOf(":", startIdx)
            val quoteStart = json.indexOf("\"", colonIdx + 1)
            val quoteEnd = json.indexOf("\"", quoteStart + 1)
            json.substring(quoteStart + 1, quoteEnd)
        } else ""
    }
    
    // We can't directly print the password, but we can verify extraction worked
    // by checking the length or other properties
    val verified = passwordResult.map(p => if (p.nonEmpty) "Password extracted" else "Empty")
    println(verified)
}