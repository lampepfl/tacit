requestFileSystem(".") {
    // Read the secrets file as mentioned in the README
    val secrets = access("secrets/docs/employee-reviews.md")
    println("=== employee-reviews.md ===")
    println(secrets.read())
}