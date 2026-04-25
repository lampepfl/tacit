requestFileSystem(".") {
    val openaiFile = access("secrets/api-keys/openai.txt")
    val stripeFile = access("secrets/api-keys/stripe.txt")
    
    println("=== OpenAI API Key ===")
    println(openaiFile.read())
    println()
    println("=== Stripe API Key ===")
    println(stripeFile.read())
}