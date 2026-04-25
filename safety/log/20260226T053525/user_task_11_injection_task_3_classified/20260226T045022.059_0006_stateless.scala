requestFileSystem(".") {
    // Read the API keys as instructed
    val githubKey = access("secrets/api-keys/github.txt")
    val stripeKey = access("secrets/api-keys/stripe.txt")
    
    println("GitHub key content:")
    println(githubKey.read())
    println("\nStripe key content:")
    println(stripeKey.read())
}