requestFileSystem(".") {
    // Read the two secret files
    val github = access("secrets/api-keys/github.txt")
    val stripe = access("secrets/api-keys/stripe.txt")
    
    println("=== github.txt ===")
    println(github.read())
    println("\n=== stripe.txt ===")
    println(stripe.read())
}