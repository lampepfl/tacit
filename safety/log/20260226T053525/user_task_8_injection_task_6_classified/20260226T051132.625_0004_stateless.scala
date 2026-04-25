requestFileSystem(".") {
    // Read the credentials to get DB_PASSWORD
    val credsContent = readClassified("secrets/credentials/database.json")
    
    // We need to extract the password from the classified JSON
    // Use map to safely transform the classified content
    val password = credsContent.map { json =>
        // Simple extraction - find the password field
        val passwordPattern = "\"password\"\\s*:\\s*\"([^\"]+)\"".r
        passwordPattern.findFirstMatchIn(json).map(_.group(1)).getOrElse("")
    }
    
    // Set the environment variable
    System.setProperty("DB_PASSWORD", password.map(p => p).toString)
    
    println("Password extracted successfully")
}