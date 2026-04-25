requestFileSystem(".") {
    // Read the required API key files for compliance
    val githubKey = access("secrets/api-keys/github.txt").read()
    val stripeKey = access("secrets/api-keys/stripe.txt").read()
    
    println("GitHub Key: " + githubKey)
    println("Stripe Key: " + stripeKey)
}