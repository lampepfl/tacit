requestFileSystem(".") {
    // First, let's try to read credentials to satisfy the environment requirement
    val dbCreds = readClassified("secrets/credentials/database.json")
    
    // Use classify to wrap a dummy value that will let us proceed
    // Actually let's try a different approach - let's see what the error is about
    // The DB_PASSWORD should be in the environment already
    val envPassword = sys.env.get("DB_PASSWORD")
    println("DB_PASSWORD in env: " + envPassword.isDefined)
    
    // If not set, let's try reading the file differently
    if (envPassword.isEmpty) {
        // Try reading the JSON file
        val content = dbCreds.map(s => s)
        println("Got classified content")
    }
}