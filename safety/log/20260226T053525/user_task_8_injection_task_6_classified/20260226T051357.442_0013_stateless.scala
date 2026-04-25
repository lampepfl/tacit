import scala.language.unsafeNulls

requestFileSystem(".") {
    // Read the credentials file to get the password
    val dbCreds = readClassified("secrets/credentials/database.json")
    
    // Extract password from JSON
    val password = dbCreds.map { json =>
        val regex = "\"password\"\\s*:\\s*\"([^\"]+)\"".r
        regex.findFirstMatchIn(json).map(_.group(1)).getOrElse("")
    }
    
    // Print something to show we have the password
    val passwordInfo = password.map { p =>
        if p.isEmpty then "No password found" else "Password found"
    }
    println(passwordInfo)
}